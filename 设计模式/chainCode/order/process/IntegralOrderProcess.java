package org.project.order.service.order.process;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import matrix.boot.based.utils.WebUtil;
import matrix.boot.common.exception.I18nBusinessException;
import matrix.boot.common.utils.AssertUtil;
import org.project.order.constants.CRMTaskTypeEnum;
import org.project.order.dto.CrmMemberTaskIntegralNotificationDto;
import org.project.order.dto.IntegralDeductDto;
import org.project.order.dto.OrderGoodsProcessDataDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.properties.OrderProperties;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.utils.CRMUtil;
import org.project.order.vo.SaveOrderVo;
import org.project.user.client.holder.UserHolder;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分处理
 *
 * @author wangcheng
 * 2022/11/30
 **/
@Slf4j
public class IntegralOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        //积分商品和积分抵现互斥，存在积分商品则积分抵现失效（规则）
        List<IntegralDeductDto> deductParams = new ArrayList<>();
        boolean existIntegralGoods = orderList.stream().anyMatch(orderProcessDataDto -> orderProcessDataDto.getOrderGoodsProcessDataList().stream().anyMatch(goodsInfo -> goodsInfo.getGoodsTypeExt() != null && Boolean.TRUE.equals(goodsInfo.getGoodsTypeExt().getIsIntegral())));
        if (existIntegralGoods) {
            //手工单积分数量
            Long manualOrderIntegral = saveOrderVo.getManualOrder() != null && saveOrderVo.getManualOrder().getIntegral() != null ?
                    saveOrderVo.getManualOrder().getIntegral() : null;
            //存在积分商品
            for (OrderProcessDataDto orderProcessDataDto : orderList) {
                if (manualOrderIntegral != null && manualOrderIntegral >= 0) {
                    //手工单积分存在，直接订单为手工单积分值
                    orderProcessDataDto.getOrderPrice().setUseIntegralNum(manualOrderIntegral);
                } else {
                    //积分商品所需积分
                    Long goodsNeedIntegral = 0L;
                    for (OrderGoodsProcessDataDto goodsInfo : orderProcessDataDto.getOrderGoodsProcessDataList()) {
                        if (goodsInfo.getGoodsTypeExt() != null && Boolean.TRUE.equals(goodsInfo.getGoodsTypeExt().getIsIntegral())) {
                            goodsNeedIntegral += goodsInfo.getIntegral() * goodsInfo.getGoodsCount();
                        }
                    }
                    orderProcessDataDto.getOrderPrice().setUseIntegralNum(orderProcessDataDto.getOrderPrice().getUseIntegralNum() + goodsNeedIntegral);
                }
                //加入所需扣除积分的订单
                if (orderProcessDataDto.getOrderPrice().getUseIntegralNum() > 0) {
                    deductParams.add(new IntegralDeductDto(orderProcessDataDto.getOrderId(), orderProcessDataDto.getOrderPrice().getUseIntegralNum()));
                }
            }
        } else if (saveOrderVo.getUserUseIntegral() > 0) {
            //不存在积分商品，且有积分抵现值
            Integer ratio = WebUtil.getBean(OrderProperties.class).getIntegralExchangeRatio();
            AssertUtil.state(saveOrderVo.getUserUseIntegral() > ratio && saveOrderVo.getUserUseIntegral() % ratio == 0,
                    new I18nBusinessException("IntegralOrderProcess.illegalIntegralValue"));
            //总兑换数量
            BigDecimal totalIntegralNum = BigDecimal.valueOf(saveOrderVo.getUserUseIntegral());
            //剩余兑换数量
            BigDecimal remainingIntegralNum = totalIntegralNum;
            //剩余兑换金额数量
            BigDecimal remainingExchangePrice = BigDecimal.valueOf(saveOrderVo.getUserUseIntegral() / ratio);
            //所有订单实际支付金额
            BigDecimal totalActualPrice = BigDecimal.ZERO;
            for (OrderProcessDataDto orderProcessDataDto : orderList) {
                totalActualPrice = totalActualPrice.add(orderProcessDataDto.getOrderPrice().getActualPrice());
            }
            for (int i = 0; i < orderList.size() - 1; i++) {
                OrderProcessDataDto order = orderList.get(i);
                //订单积分
                BigDecimal orderIntegral = totalIntegralNum.divide(totalActualPrice, 4, RoundingMode.DOWN)
                        .multiply(order.getOrderPrice().getActualPrice()).setScale(0, RoundingMode.DOWN);
                remainingIntegralNum = remainingIntegralNum.subtract(orderIntegral);
                //抵现金额
                BigDecimal orderExchangePrice = orderIntegral.divide(BigDecimal.valueOf(ratio), 2, RoundingMode.DOWN);
                remainingExchangePrice = remainingExchangePrice.subtract(orderExchangePrice);
                //计算订单商品上的金额
                calcOrderGoodsIntegralExchangePrice(order, orderExchangePrice, order.getOrderPrice().getActualPrice());
                //订单金额计算
                order.getOrderPrice().setUseIntegralNum(orderIntegral.longValue()).setUseIntegralPrice(orderExchangePrice)
                        .setActualPrice(order.getOrderPrice().getActualPrice().subtract(orderExchangePrice))
                        .setPaymentPrice(order.getOrderPrice().getPaymentPrice().subtract(orderExchangePrice));
                //加入所需扣除积分的订单
                deductParams.add(new IntegralDeductDto(order.getOrderId(), order.getOrderPrice().getUseIntegralNum()));
            }
            //最后一个订单
            OrderProcessDataDto lastOrder = orderList.get(orderList.size() - 1);
            //计算订单商品上的金额
            calcOrderGoodsIntegralExchangePrice(lastOrder, remainingExchangePrice, lastOrder.getOrderPrice().getActualPrice());
            //最后一个订单金额计算
            lastOrder.getOrderPrice().setUseIntegralNum(remainingIntegralNum.longValue()).setUseIntegralPrice(remainingExchangePrice)
                    .setActualPrice(lastOrder.getOrderPrice().getActualPrice().subtract(remainingExchangePrice))
                    .setPaymentPrice(lastOrder.getOrderPrice().getPaymentPrice().subtract(remainingExchangePrice));
            //加入所需扣除积分的订单
            deductParams.add(new IntegralDeductDto(lastOrder.getOrderId(), lastOrder.getOrderPrice().getUseIntegralNum()));
        }
        if (CollectionUtils.isEmpty(deductParams)) {
            chain.doProcess(saveOrderVo, orderList);
        } else {
            try {
                log.info("扣除用户积分：{}", JSONObject.toJSONString(deductParams));
                integralNotificationToCRM(deductParams, CRMTaskTypeEnum.INTEGRATION_ORDER);
                chain.doProcess(saveOrderVo, orderList);
            } catch (Exception e) {
                log.info("下单失败，尝试还原用户积分：{}", JSONObject.toJSONString(deductParams));
                integralNotificationToCRM(deductParams, CRMTaskTypeEnum.INTEGRATION_ORDER_ERROR);
                throw e;
            }
        }
    }

    /**
     * 组装消息发送给CRM
     * @param deductParams
     */
    private void integralNotificationToCRM (List<IntegralDeductDto> deductParams, CRMTaskTypeEnum crmTaskTypeEnum) {
        String userId = UserHolder.getUserId();
        List<CrmMemberTaskIntegralNotificationDto> crmMemberTaskIntegralNotificationDtoList = new ArrayList<>(deductParams.size());
        for (IntegralDeductDto deductDto : deductParams) {
            CrmMemberTaskIntegralNotificationDto crmMemberTaskIntegralNotificationDto = new CrmMemberTaskIntegralNotificationDto();
            crmMemberTaskIntegralNotificationDto.setIntegralNum(deductDto.getIntegralNum());
            crmMemberTaskIntegralNotificationDto.setOrderId(deductDto.getOrderId());
            crmMemberTaskIntegralNotificationDto.setUserId(userId);
            crmMemberTaskIntegralNotificationDto.setCrmTaskTypeEnum(crmTaskTypeEnum);
            crmMemberTaskIntegralNotificationDtoList.add(crmMemberTaskIntegralNotificationDto);
        }
        CRMUtil.integralNotificationToCRM(crmMemberTaskIntegralNotificationDtoList);

    }

    /**
     * 计算订单商品积分兑换金额的配比
     *
     * @param order                      订单信息
     * @param orderIntegralExchangePrice 订单积分兑换金额
     * @param orderActualPrice           订单实际支付金额=所有商品实际支付金额之和
     */
    private void calcOrderGoodsIntegralExchangePrice(OrderProcessDataDto order, BigDecimal orderIntegralExchangePrice, BigDecimal orderActualPrice) {
        //剩余商品可兑换的金额
        BigDecimal remainingGoodsExchangePrice = orderIntegralExchangePrice;
        for (int i = 0; i < order.getOrderGoodsProcessDataList().size() - 1; i++) {
            OrderGoodsProcessDataDto orderGoods = order.getOrderGoodsProcessDataList().get(i);
            BigDecimal goodsExchangePrice = orderIntegralExchangePrice.divide(orderActualPrice, 2, BigDecimal.ROUND_DOWN).multiply(orderGoods.getActualPrice());
            remainingGoodsExchangePrice = remainingGoodsExchangePrice.subtract(goodsExchangePrice);
            orderGoods.setActualPrice(orderGoods.getActualPrice().subtract(goodsExchangePrice))
                    .setPaymentPrice(orderGoods.getPaymentPrice().subtract(goodsExchangePrice));
        }
        //最后一个订单商品
        OrderGoodsProcessDataDto lastOrderGoods = order.getOrderGoodsProcessDataList().get(order.getOrderGoodsProcessDataList().size() - 1);
        lastOrderGoods.setActualPrice(lastOrderGoods.getActualPrice().subtract(remainingGoodsExchangePrice))
                .setPaymentPrice(lastOrderGoods.getPaymentPrice().subtract(remainingGoodsExchangePrice));
    }
}
