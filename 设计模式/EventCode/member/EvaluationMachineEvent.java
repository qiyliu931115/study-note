package com.yf.ShoppingCart.web.event.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * 商品评价（机器or大件）事件
 */
@Getter
@Setter
public class EvaluationMachineEvent extends ApplicationEvent {

    /**
     * 用户token
     */
    private String token;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 订单商品实付金额
     */
    private BigDecimal orderGoodsPaymentAmount;

    public EvaluationMachineEvent(Object source) {
        super(source);
    }

}
