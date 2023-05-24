package org.project.order.service.order.process;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import matrix.boot.based.utils.I18nMessageUtil;
import matrix.boot.based.utils.WebUtil;
import matrix.boot.common.exception.I18nBusinessException;
import matrix.boot.common.exception.ServiceException;
import matrix.boot.common.utils.AssertUtil;
import matrix.boot.common.utils.DateUtil;
import matrix.boot.common.utils.StringUtil;
import org.project.basic.client.utils.FeignResultUtil;
import org.project.goods.client.dto.CombinationGoodsForOrderDto;
import org.project.goods.client.dto.GoodsForOrderDto;
import org.project.goods.client.enums.BaseGoodsTypeEnum;
import org.project.goods.client.feign.GoodsClient;
import org.project.order.constants.CacheKeyConstant;
import org.project.order.dto.OrderGoodsProcessDataDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.entity.UserCartEntity;
import org.project.order.events.OrderSuccessEvent;
import org.project.order.service.UserCartService;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderGoodsVo;
import org.project.order.vo.SaveOrderVo;
import org.project.shop.client.dto.ShopDto;
import org.project.shop.client.feign.ShopClient;
import org.project.user.client.dto.UserAddressDto;
import org.project.user.client.feign.UserAddressClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单前置数据处理
 *
 * @author wangcheng
 * date 2022/11/25
 */
@Slf4j
public class BeforeOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, final List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        //下单事务此处开启
        TransactionTemplate transactionTemplate = WebUtil.getBean(TransactionTemplate.class);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                //获取并删除购物车数据
                List<UserCartEntity> userCartEntities = WebUtil.getBean(UserCartService.class).queryAndRemoveUserCarts(saveOrderVo.getCartIds());
                //订单商品列表
                List<OrderGoodsProcessDataDto> orderGoodsList = null;
                if (!CollectionUtils.isEmpty(saveOrderVo.getOrderGoodsList())) {
                    //用户传入商品不为空
                    orderGoodsList = convertForSaveOrderGoods(saveOrderVo.getOrderGoodsList());
                } else if (!CollectionUtils.isEmpty(userCartEntities)) {
                    //购物车存在商品
                    orderGoodsList = convertForCart(userCartEntities);
                }
                AssertUtil.state(!CollectionUtils.isEmpty(orderGoodsList), new I18nBusinessException("BeforeOrderProcess.noOrderGoods"));
                assert orderGoodsList != null;
                //补充下单商品数据
                fillOrderGoodsProcessData(orderGoodsList);
                //补充组合商品数据信息
                fillCombinationGoods(orderGoodsList);
                //开始拆单，店铺订单商品列表字典项
                splitOrderAndFillExtInfo(saveOrderVo, orderList, orderGoodsList);
                //获取redis模板
                StringRedisTemplate redisTemplate = WebUtil.getBean(StringRedisTemplate.class);
                //需要保存进redis中的信息
                Map<String, String> multiSaveMap = new HashMap<>();
                orderList.forEach(orderProcessData -> multiSaveMap.put(CacheKeyConstant.ORDER_CREATING_PREFIX + orderProcessData.getOrderId(), DateUtil.format(DateUtil.getNowDate(), DateUtil.STANDARD_FORMATTER)));
                try {
                    //保存订单号进入redis表示正在创建订单
                    redisTemplate.opsForValue().multiSet(multiSaveMap);
                    //进入下一个链条
                    chain.doProcess(saveOrderVo, orderList);
                } finally {
                    //删除redis正在创建订单的标识
                    redisTemplate.delete(multiSaveMap.keySet());
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
        //发送下单成功通知事件
        WebUtil.getApplicationContext().publishEvent(new OrderSuccessEvent(orderList));
    }

    /**
     * 购物车实体 -> 订单商品处理数据
     *
     * @param userCartEntities 购物车实体列表
     * @return 订单商品处理数据列表
     */
    private List<OrderGoodsProcessDataDto> convertForCart(List<UserCartEntity> userCartEntities) {
        List<OrderGoodsProcessDataDto> result = new ArrayList<>();
        userCartEntities.forEach(userCartEntity -> result.add(new OrderGoodsProcessDataDto()
                .setGoodsId(userCartEntity.getGoodsId()).setGoodsCount(userCartEntity.getGoodsCount())));
        return result;
    }

    /**
     * 用户商品实体 -> 订单商品处理数据
     *
     * @param goodsList 商品列表
     * @return 订单商品处理数据列表
     */
    private List<OrderGoodsProcessDataDto> convertForSaveOrderGoods(List<SaveOrderGoodsVo> goodsList) {
        List<OrderGoodsProcessDataDto> result = new ArrayList<>();
        goodsList.forEach(goods -> result.add(new OrderGoodsProcessDataDto()
                .setGoodsId(goods.getGoodsId()).setGoodsCount(goods.getGoodsCount())));
        return result;
    }

    /**
     * 填充订单商品处理数据
     *
     * @param orderGoodsList 订单商品列表
     */
    private void fillOrderGoodsProcessData(List<OrderGoodsProcessDataDto> orderGoodsList) {
        //补充下单商品数据
        List<GoodsForOrderDto> goodsInfoList = FeignResultUtil.getData(WebUtil.getBean(GoodsClient.class).obtainGoodsForOrder(orderGoodsList.stream().map(OrderGoodsProcessDataDto::getGoodsId).collect(Collectors.toList())));
        //key -> goodsId
        Map<String, GoodsForOrderDto> goodsInfoMap = goodsInfoList.stream().collect(Collectors.toMap(GoodsForOrderDto::getGoodsId, item -> item, (o1, o2) -> o2));
        orderGoodsList.forEach(orderGoodsProcessDataDto -> {
            GoodsForOrderDto goodsInfo = goodsInfoMap.get(orderGoodsProcessDataDto.getGoodsId());
            AssertUtil.state(goodsInfo != null, String.format("商品: %s, 状态异常", orderGoodsProcessDataDto.getGoodsId()));
            assert goodsInfo != null;
            BeanUtils.copyProperties(goodsInfo, orderGoodsProcessDataDto);
            //设置商品现金支付金额
            orderGoodsProcessDataDto.setPaymentPrice(orderGoodsProcessDataDto.getGoodsPrice().multiply(BigDecimal.valueOf(orderGoodsProcessDataDto.getGoodsCount())));
            //设置商品实际支付金额=商品现金支付金额
            orderGoodsProcessDataDto.setActualPrice(orderGoodsProcessDataDto.getPaymentPrice());
        });
    }

    /**
     * 补充组合商品数据信息
     *
     * @param orderGoodsList 订单商品列表
     */
    private void fillCombinationGoods(List<OrderGoodsProcessDataDto> orderGoodsList) {
        //组合商品IDs
        List<String> combinationGoodsIds = orderGoodsList.stream().filter(orderGoodsProcessDataDto -> Boolean.TRUE.equals(orderGoodsProcessDataDto.getCombination())).map(OrderGoodsProcessDataDto::getGoodsId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(combinationGoodsIds)) {
            return;
        }
        //组合商品列表
        List<CombinationGoodsForOrderDto> combinationGoodsForOrderList = FeignResultUtil.getData(WebUtil.getBean(GoodsClient.class).obtainCombinationGoodsForOrder(combinationGoodsIds));
        //组合商品列表字典项（key -> 组合商品ID）
        Map<String, List<CombinationGoodsForOrderDto>> combinationGoodsForOrderListMap = new HashMap<>();
        combinationGoodsForOrderList.forEach(combinationGoodsForOrder -> combinationGoodsForOrderListMap.computeIfAbsent(combinationGoodsForOrder.getCombinationGoodsId(), k -> new ArrayList<>()).add(combinationGoodsForOrder));
        orderGoodsList.forEach(orderGoodsProcessData -> {
            if (Boolean.TRUE.equals(orderGoodsProcessData.getCombination())) {
                orderGoodsProcessData.setCombinationGoodsList(combinationGoodsForOrderListMap.get(orderGoodsProcessData.getGoodsId()));
            }
        });
    }

    /**
     * 开始拆单，并补充订单附加数据
     *
     * @param saveOrderVo    保存订单参数
     * @param orderList      订单列表
     * @param orderGoodsList 订单商品列表
     */
    private void splitOrderAndFillExtInfo(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, List<OrderGoodsProcessDataDto> orderGoodsList) {
        //定义需生成的订单列表
        Map<String, List<OrderGoodsProcessDataDto>> shopOrderGoodsListMap = new HashMap<>();
        orderGoodsList.forEach(orderGoodsProcessData -> shopOrderGoodsListMap.computeIfAbsent(orderGoodsProcessData.getShopId(), k -> new ArrayList<>()).add(orderGoodsProcessData));
        //用户地址信息获取
        UserAddressDto userAddress = null;
        if (!StringUtil.isEmpty(saveOrderVo.getAddressId())) {
            userAddress = FeignResultUtil.getData(WebUtil.getBean(UserAddressClient.class).detailById(saveOrderVo.getAddressId()));
        } else if (saveOrderVo.getManualOrder() != null && saveOrderVo.getManualOrder().getUserAddress() != null) {
            userAddress = saveOrderVo.getManualOrder().getUserAddress();
        }
        //查询店铺信息
        Map<String, ShopDto> shopMap = FeignResultUtil.getData(WebUtil.getBean(ShopClient.class).obtainShopMap(shopOrderGoodsListMap.keySet()));
        //生成的订单号列表
        for (String shopId : shopOrderGoodsListMap.keySet()) {
            List<OrderGoodsProcessDataDto> orderGoodsProcessDataList = shopOrderGoodsListMap.get(shopId);
            //获取店铺
            ShopDto shop = shopMap.get(shopId);
            AssertUtil.state(shop != null, String.format("店铺:%s, 不存在", shopId));
            assert shop != null;
            OrderProcessDataDto item = new OrderProcessDataDto();
            BeanUtils.copyProperties(saveOrderVo, item);
            item.setShopId(shopId).setShopName(shop.getName()).setCountryCode(shop.getCountryCode());
            //生成订单号
            item.setOrderId(IdWorker.getIdStr());
            //设置商品数据
            item.setOrderGoodsProcessDataList(orderGoodsProcessDataList);
            //实物商品数量
            long count = orderGoodsProcessDataList.stream().filter(orderGoodsProcessDataDto -> BaseGoodsTypeEnum.NORMAL.name().equals(orderGoodsProcessDataDto.getGoodsType())).count();
            if (count > 0) {
                //存在实物商品，赋值用户地址信息
                AssertUtil.state(userAddress != null, new I18nBusinessException("BeforeOrderProcess.noReceiveAddress"));
                assert userAddress != null;
                item.setUserAddress(userAddress);
            } else {
                //纯虚拟商品下单，无须填写物流信息，赋值为空地址
                item.setUserAddress(new UserAddressDto()
                        .setId(StringUtil.EMPTY_STRING).setName(StringUtil.EMPTY_STRING).setMobile(StringUtil.EMPTY_STRING)
                        .setProvince(StringUtil.EMPTY_STRING).setCity(StringUtil.EMPTY_STRING)
                        .setDistrict(StringUtil.EMPTY_STRING).setAddress(StringUtil.EMPTY_STRING)
                        .setZipcode(StringUtil.EMPTY_STRING).setIsDefault(false));
            }
            //设置发票数据
            item.setUserInvoice(saveOrderVo.getUserInvoice());
            //买家留言信息
            if (!CollectionUtils.isEmpty(saveOrderVo.getBuyerRemarkMap())) {
                item.setBuyerRemark(saveOrderVo.getBuyerRemarkMap().get(shopId));
            }
            orderList.add(item);
        }
    }
}
