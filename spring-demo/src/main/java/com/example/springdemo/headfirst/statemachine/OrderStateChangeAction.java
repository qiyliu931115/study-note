package com.example.springdemo.headfirst.statemachine;

public enum OrderStateChangeAction {

    /**
     * 支付操作 待支付-》待发货
     */
    PAY_ORDER,

    /**
     * 发货操作 待发货-》待收货
     */
    SEND_ORDER,

    /**
     * 收货操作 待收货-》订单完成
     */
    RECEIVE_ORDER,

    ;
}
