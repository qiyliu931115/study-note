package com.example.springdemo.headfirst.statemachine;


public class Order {

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单类型
     */
    private OrderState orderState;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }
}
