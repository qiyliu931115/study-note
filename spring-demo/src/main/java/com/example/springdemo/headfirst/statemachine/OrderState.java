package com.example.springdemo.headfirst.statemachine;

/**
 * 订单状态枚举
 */
public enum OrderState {
    /**
     * 待支付
     */
    ORDER_WAIT_PAY,

    /**
     * 待发货
     */
    ORDER_WAIT_SEND,

    /**
     * 待收货
     */
    ORDER_WAIT_RECEIVE,
    /**
     * 订单完成
     */
    ORDER_FINISH,

    ;
}
