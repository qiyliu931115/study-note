package com.yf.ShoppingCart.web.event.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 订单确认收货事件处理
 */
@Getter
@Setter
public class OrderConfirmReceiptOfGoodsEventEvent extends ApplicationEvent {

    /**
     * 订单ID
     */
    private String orderId;

    public OrderConfirmReceiptOfGoodsEventEvent(Object source) {
        super(source);
    }
}
