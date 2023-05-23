package com.example.springdemo.headfirst.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

public class OrderService {

    @Autowired
    private StateMachine<OrderState, OrderStateChangeAction> stateMachine;

    @Autowired
    private RedisStateMachinePersister<OrderState, OrderStateChangeAction>  redisStateMachinePersister;

    @Autowired
    private RedisTemplate<String, String> stringStringRedisTemplate;

    /**
     * 支付回调
     * @param orderId
     * @return
     */
    public Order pay (String orderId) {
        //从Redis获取订单信息
        Order order = null;
        //order = (Order) stringStringRedisTemplate.opsForValue().get(orderId);

        //包装订单状态变更message 附带订单操作pay_order
        Message<OrderStateChangeAction> message = MessageBuilder.withPayload(OrderStateChangeAction.PAY_ORDER)
                .setHeader("order", order).build();

        //传递给状态机
        if (changeStateAction(message, order)) {
            return order;
        }
        return null;
    }

    public boolean changeStateAction(Message<OrderStateChangeAction> message, Order order) {
        try {
            String key = order.getOrderId() + "STATE";
            //启动状态机
            stateMachine.start();
            //从redis读取状态机 缓存key（自定义）
            redisStateMachinePersister.restore(stateMachine, key);
            //发送给OrderStateListener
            boolean result = stateMachine.sendEvent(message);
            //将更改完订单状态的 状态机 存储到 redis缓存
            redisStateMachinePersister.persist(stateMachine, key);
            return result;

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            stateMachine.stop();
        }
        return false;
    }
}
