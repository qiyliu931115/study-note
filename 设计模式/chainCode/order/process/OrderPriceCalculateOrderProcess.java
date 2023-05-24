package org.project.order.service.order.process;

import lombok.extern.slf4j.Slf4j;
import org.project.order.dto.OrderPriceDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderVo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 订单金额计算处理
 *
 * @author wangcheng
 * date 2022/11/29
 */
@Slf4j
public class OrderPriceCalculateOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        orderList.forEach(orderProcessData -> {
            OrderPriceDto orderPrice = orderProcessData.getOrderPrice();
            orderProcessData.getOrderGoodsProcessDataList().forEach(orderGoodsProcessData -> {
                //商品击穿底价校验
                if (orderGoodsProcessData.getLowSalePrice() != null) {
                    //实际商品单价
                    BigDecimal actualGoodsPrice = orderGoodsProcessData.getActualPrice().divide(BigDecimal.valueOf(orderGoodsProcessData.getGoodsCount()), 2, RoundingMode.HALF_UP);
                    if (actualGoodsPrice.compareTo(orderGoodsProcessData.getLowSalePrice()) < 0) {
                        log.info("商品ID:{}, 已击穿底价，系统自动修复", orderGoodsProcessData.getGoodsId());
                        //最低真实价格
                        BigDecimal lowActualPrice = orderGoodsProcessData.getLowSalePrice().multiply(BigDecimal.valueOf(orderGoodsProcessData.getGoodsCount()));
                        //差价
                        BigDecimal calcPrice = lowActualPrice.subtract(orderGoodsProcessData.getActualPrice());
                        if (calcPrice.compareTo(BigDecimal.ZERO) > 0) {
                            //double check
                            orderGoodsProcessData.setActualPrice(lowActualPrice);
                            orderGoodsProcessData.setPaymentPrice(orderGoodsProcessData.getPaymentPrice().add(calcPrice));
                        }
                    }
                }
                //计算总金额
                orderPrice.setPrice(orderPrice.getPrice().add(orderGoodsProcessData.getGoodsPrice().multiply(BigDecimal.valueOf(orderGoodsProcessData.getGoodsCount()))));
                //现金支付金额
                orderPrice.setPaymentPrice(orderPrice.getPaymentPrice().add(orderGoodsProcessData.getPaymentPrice()));
                //实际支付金额
                orderPrice.setActualPrice(orderPrice.getActualPrice().add(orderGoodsProcessData.getActualPrice()));
            });
            //运费需支付金额
            BigDecimal shippingPrice = orderPrice.getShippingPrice().subtract(orderPrice.getUseShippingDiscountPrice());
            //现金支付金额=现金支付金额+运费金额
            orderPrice.setPaymentPrice(orderPrice.getPaymentPrice().add(shippingPrice));
            //实际支付金额=商品实际支付金额+运费金额
            orderPrice.setActualPrice(orderPrice.getActualPrice().add(shippingPrice));
        });
        chain.doProcess(saveOrderVo, orderList);
    }

}
