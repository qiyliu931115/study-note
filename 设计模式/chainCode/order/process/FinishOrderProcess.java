package org.project.order.service.order.process;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import matrix.boot.based.utils.I18nMessageUtil;
import matrix.boot.based.utils.WebUtil;
import matrix.boot.common.exception.I18nBusinessException;
import matrix.boot.common.utils.AssertUtil;
import matrix.boot.common.utils.StringUtil;
import org.project.goods.client.dto.CombinationGoodsForOrderDto;
import org.project.goods.client.dto.GoodsForOrderDto;
import org.project.order.client.enums.OrderStatusEnum;
import org.project.order.dto.OrderGoodsProcessDataDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.entity.OrderCombinationGoodsEntity;
import org.project.order.entity.OrderEntity;
import org.project.order.entity.OrderExtEntity;
import org.project.order.entity.OrderGoodsEntity;
import org.project.order.enums.EvaluateStatusEnum;
import org.project.order.enums.OrderGoodsRefundStatusEnum;
import org.project.order.enums.OrderInvoiceStatusEnum;
import org.project.order.enums.OrderRefundStatusEnum;
import org.project.order.service.OrderCombinationGoodsService;
import org.project.order.service.OrderExtService;
import org.project.order.service.OrderGoodsService;
import org.project.order.service.OrderService;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderVo;
import org.project.user.client.dto.UserAddressDto;
import org.project.user.client.holder.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangcheng
 * 2022/11/30
 **/
@Slf4j
public class FinishOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        //订单数据
        List<OrderEntity> orderEntities = new ArrayList<>();
        //订单附加数据
        List<OrderExtEntity> orderExtEntities = new ArrayList<>();
        //订单商品数据
        List<OrderGoodsEntity> orderGoodsEntities = new ArrayList<>();
        //订单组合商品数据
        List<OrderCombinationGoodsEntity> orderCombinationGoodsEntities = new ArrayList<>();
        //用户ID
        String userId = UserHolder.getUserId();
        if (saveOrderVo.getManualOrder() != null && !StringUtil.isEmpty(saveOrderVo.getManualOrder().getUserId())) {
            //手工单用户存在，放入用户
            userId = saveOrderVo.getManualOrder().getUserId();
        }
        //遍历订单数据
        for (OrderProcessDataDto orderProcessData : orderList) {
            OrderEntity orderEntity = convertOrder(userId, orderProcessData);
            orderEntities.add(orderEntity);
            orderExtEntities.add(convertOrderExt(orderProcessData));
            orderProcessData.getOrderGoodsProcessDataList().forEach(orderGoodsProcessData -> {
                //转换订单商品实体，优先生成ID
                OrderGoodsEntity orderGoodsEntity = convertOrderGoods(orderProcessData.getOrderId(), orderGoodsProcessData);
                orderGoodsEntities.add(orderGoodsEntity);
                if (Boolean.TRUE.equals(orderGoodsProcessData.getCombination()) && !CollectionUtils.isEmpty(orderGoodsProcessData.getCombinationGoodsList())) {
                    //填充组合商品
                    List<OrderCombinationGoodsEntity> combinationGoodsEntities = conventCombinationGoodsEntity(orderEntity.getId(), orderGoodsEntity.getId(), orderGoodsProcessData);
                    if (!CollectionUtils.isEmpty(combinationGoodsEntities)) {
                        orderCombinationGoodsEntities.addAll(combinationGoodsEntities);
                    }
                }
            });
        }
        OrderService orderService = WebUtil.getBean(OrderService.class);
        orderService.saveBatch(orderEntities);
        WebUtil.getBean(OrderExtService.class).saveBatch(orderExtEntities);
        WebUtil.getBean(OrderGoodsService.class).saveBatch(orderGoodsEntities);
        //保存组合商品
        if (!CollectionUtils.isEmpty(orderCombinationGoodsEntities)) {
            WebUtil.getBean(OrderCombinationGoodsService.class).saveBatch(orderCombinationGoodsEntities);
        }
        chain.doProcess(saveOrderVo, orderList);
    }

    /**
     * 转换订单实体
     *
     * @param userId           用户ID
     * @param orderProcessData 订单处理数据
     * @return 订单实体
     */
    private OrderEntity convertOrder(String userId, OrderProcessDataDto orderProcessData) {
        OrderEntity orderEntity = new OrderEntity();
        OrderStatusEnum orderStatus = OrderStatusEnum.WAIT_PAY;
        if (BigDecimal.ZERO.compareTo(orderProcessData.getOrderPrice().getPaymentPrice()) >= 0) {
            //0元购，直接订单已支付
            orderStatus = OrderStatusEnum.PAYED;
        }
        BeanUtils.copyProperties(orderProcessData, orderEntity);
        BeanUtils.copyProperties(orderProcessData.getOrderPrice(), orderEntity);
        orderEntity.setUserId(userId)
                .setRefundPrice(BigDecimal.ZERO)
                .setOrderStatus(orderStatus.getCode())
                .setOrderRefundStatus(OrderRefundStatusEnum.NO_REFUND.getCode())
                .setOrderInvoiceStatus(OrderInvoiceStatusEnum.NO_CREATE_INVOICE.getCode())
                .setEvaluateStatus(EvaluateStatusEnum.NO_EVALUATE.getCode())
                .setFrontDeleted(false);
        orderEntity.setId(orderProcessData.getOrderId());
        //设置原数据订单状态，用于事件
        orderProcessData.setOrderStatus(orderStatus.getCode());
        return orderEntity;
    }

    /**
     * 转换订单附加实体
     *
     * @param orderProcessData 订单处理数据
     * @return 订单附加实体
     */
    private OrderExtEntity convertOrderExt(OrderProcessDataDto orderProcessData) {
        OrderExtEntity orderExtEntity = new OrderExtEntity();
        BeanUtils.copyProperties(orderProcessData, orderExtEntity);
        UserAddressDto userAddress = orderProcessData.getUserAddress();
        orderExtEntity.setReceiverName(userAddress.getName())
                .setReceiverMobile(userAddress.getMobile())
                .setReceiverProvince(userAddress.getProvince())
                .setReceiverCity(userAddress.getCity())
                .setReceiverDistrict(userAddress.getDistrict())
                .setReceiverZipcode(userAddress.getZipcode())
                .setReceiverAddress(userAddress.getAddress());
        if (Boolean.TRUE.equals(orderProcessData.getNeedInvoice()) && orderProcessData.getUserInvoice() != null) {
            AssertUtil.state(!StringUtil.isEmpty(orderProcessData.getUserInvoice().getInvoiceTitle()), new I18nBusinessException("FinishOrderProcess.invoiceTitleNotNull"));
            AssertUtil.state(orderProcessData.getUserInvoice().getInvoiceType() != null, new I18nBusinessException("FinishOrderProcess.invoiceTypeNotNull"));
            BeanUtils.copyProperties(orderProcessData.getUserInvoice(), orderExtEntity, "id");
            //设置开票成功后的通知人
            orderExtEntity.setInvoiceReceiverMobile(orderProcessData.getInvoiceReceiverMobile())
                    .setInvoiceReceiverEmail(orderProcessData.getInvoiceReceiverEmail());
        }
        return orderExtEntity;
    }

    /**
     * 转换为订单商品
     *
     * @param orderId               订单ID
     * @param orderGoodsProcessData 订单商品处理数据
     * @return 订单商品
     */
    private OrderGoodsEntity convertOrderGoods(String orderId, OrderGoodsProcessDataDto orderGoodsProcessData) {
        OrderGoodsEntity orderGoodsEntity = new OrderGoodsEntity()
                .setOrderId(orderId);
        orderGoodsEntity.setId(IdWorker.getIdStr());
        BeanUtils.copyProperties(orderGoodsProcessData, orderGoodsEntity);
        orderGoodsEntity.setGoodsTypeExt(JSONObject.toJSONString(orderGoodsProcessData.getGoodsTypeExt()))
                .setRefundPrice(BigDecimal.ZERO).setRefundStatus(OrderGoodsRefundStatusEnum.NO_REFUND.getCode());
        return orderGoodsEntity;
    }

    /**
     * 转换组合商品实体
     *
     * @param orderId               订单ID
     * @param orderGoodsId          订单商品ID
     * @param orderGoodsProcessData 订单商品数据
     * @return 组合商品实体
     */
    private List<OrderCombinationGoodsEntity> conventCombinationGoodsEntity(String orderId, String orderGoodsId, OrderGoodsProcessDataDto orderGoodsProcessData) {
        //组合商品列表
        List<CombinationGoodsForOrderDto> combinationGoodsList = orderGoodsProcessData.getCombinationGoodsList();
        if (CollectionUtils.isEmpty(combinationGoodsList)) {
            return null;
        }
        List<OrderCombinationGoodsEntity> result = new ArrayList<>();
        combinationGoodsList.forEach(combinationGoodsForOrder -> {
            //获取商品信息
            GoodsForOrderDto goodsInfo = combinationGoodsForOrder.getSonGoodsInfo();
            result.add(new OrderCombinationGoodsEntity().setOrderId(orderId)
                    .setOrderGoodsId(orderGoodsId)
                    .setGoodsId(combinationGoodsForOrder.getCombinationGoodsId())
                    .setGoodsCount(orderGoodsProcessData.getGoodsCount())
                    .setSonGoodsId(combinationGoodsForOrder.getSonGoodsId())
                    .setSonGoodsType(goodsInfo.getGoodsType())
                    .setSonGoodsTypeExt(JSONObject.toJSONString(orderGoodsProcessData.getGoodsTypeExt()))
                    .setSonGoodsCode(goodsInfo.getGoodsCode())
                    .setSonGoodsName(goodsInfo.getGoodsName())
                    .setSonGoodsCount(combinationGoodsForOrder.getSonGoodsNum())
                    .setSonGoodsSkuInfo(goodsInfo.getGoodsSkuInfo())
                    .setSonGoodsListImage(goodsInfo.getGoodsListImage())
                    .setSonGoodsMirror(goodsInfo.getGoodsMirror())
                    .setSonGoodsPrice(goodsInfo.getGoodsPrice())
                    .setSonGoodsIntegral(goodsInfo.getIntegral()));
        });
        //计算实际支付金额和现金支付金额
        WebUtil.getBean(OrderCombinationGoodsService.class).calcPrice(result, orderGoodsProcessData.getActualPrice(), orderGoodsProcessData.getPaymentPrice());
        return result;

    }

}
