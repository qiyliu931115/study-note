package com.example.springdemo.headfirst.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

/**
 * 状态转化时进行监听 观察者模式
 */
@Component
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListener {

    @Autowired
    private RedisTemplate<String, String> stringStringRedisTemplate;

    @OnTransition(source = "ORDER_WAIT_PAY", target = "ORDER_WAIT_SEND")
    public boolean payToSend (Message<OrderStateChangeAction> message) {


        //从redis获取订单状态， 判断当前订单状态是否为待支付
        Order order = (Order) message.getHeaders().get("order");
        if (order.getOrderState() != OrderState.ORDER_WAIT_PAY) {
            throw new UnsupportedOperationException("Order State Error!");
        }

        //支付逻辑 微信 支付宝

        // 支付成功后修改 订单状态为待发货 并更新redis缓存
        order.setOrderState(OrderState.ORDER_WAIT_SEND);

        return true;
    }

}
