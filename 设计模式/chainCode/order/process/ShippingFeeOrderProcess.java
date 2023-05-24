package org.project.order.service.order.process;

import lombok.extern.slf4j.Slf4j;
import matrix.boot.based.utils.WebUtil;
import matrix.boot.common.utils.AssertUtil;
import org.project.basic.client.utils.FeignResultUtil;
import org.project.goods.client.enums.BaseGoodsTypeEnum;
import org.project.order.dto.OrderGoodsProcessDataDto;
import org.project.order.dto.OrderPriceDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderVo;
import org.project.shop.client.dto.ShippingFeeStrategyDto;
import org.project.shop.client.feign.ShippingFeeStrategyClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运费处理
 *
 * @author wangcheng
 * date 2022/11/29
 */
@Slf4j
public class ShippingFeeOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        ShippingFeeStrategyClient shippingFeeStrategyClient = WebUtil.getBean(ShippingFeeStrategyClient.class);
        List<String> shopIds = orderList.stream().map(OrderProcessDataDto::getShopId).collect(Collectors.toList());
        //查询店铺运费策略
        List<ShippingFeeStrategyDto> shippingFeeStrategyList = FeignResultUtil.getData(shippingFeeStrategyClient.obtainByShopIds(shopIds));
        //运费策略Map,key->shopId
        Map<String, ShippingFeeStrategyDto> shippingFeeStrategyMap = shippingFeeStrategyList.stream().collect(Collectors.toMap(ShippingFeeStrategyDto::getShopId, item -> item, (o1, o2) -> o2));
        orderList.forEach(orderProcessData -> {
            //运费策略
            ShippingFeeStrategyDto shippingFeeStrategy = shippingFeeStrategyMap.get(orderProcessData.getShopId());
            AssertUtil.state(shippingFeeStrategy != null, String.format("店铺:%s, 未找到运费策略", orderProcessData.getShopId()));
            assert shippingFeeStrategy != null;
            BigDecimal actualPrice = BigDecimal.ZERO;
            //是否存在实物商品(是否需要计算运费)
            boolean needCalcShippingFee = false;
            for (OrderGoodsProcessDataDto orderGoodsProcessData : orderProcessData.getOrderGoodsProcessDataList()) {
                actualPrice = actualPrice.add(orderGoodsProcessData.getActualPrice());
                //判断是否需要计算运费(实物商品计算运费)
                if (!needCalcShippingFee && BaseGoodsTypeEnum.NORMAL.name().equals(orderGoodsProcessData.getGoodsType())) {
                    needCalcShippingFee = true;
                }
            }
            OrderPriceDto orderPrice = orderProcessData.getOrderPrice();
            if (needCalcShippingFee && shippingFeeStrategy.getFreeShippingPrice().compareTo(actualPrice) > 0) {
                //设置运费金额
                orderPrice.setShippingPrice(shippingFeeStrategy.getFixedShippingPrice());
                if (orderPrice.getUseShippingDiscountPrice().compareTo(BigDecimal.ZERO) < 0) {
                    //全额抵扣(将-1转为实际抵扣金额)
                    orderPrice.setUseShippingDiscountPrice(shippingFeeStrategy.getFixedShippingPrice());
                }
            }
        });
        chain.doProcess(saveOrderVo, orderList);
    }
}
