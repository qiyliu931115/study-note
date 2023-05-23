package com.example.springdemo.headfirst.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

public class OrderService {

//    @Autowired
//    private StateMachine<OrderState, OrderStateChangeAction> stateMachine;

    public static final String stateMachineId = "orderStateMachine";

    @Autowired
    private StateMachineFactory<OrderState, OrderStateChangeAction> orderStateMachineFactory;

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


    /**
     * 发送事件
     * @param message
     * @param order
     * @return
     */
    public boolean changeStateAction(Message<OrderStateChangeAction> message, Order order) {
        StateMachine<OrderState, OrderStateChangeAction> stateMachine = null;

        try {
            String key = order.getOrderId() + "STATE";

//            状态机都是有状态的（Stateful）的，有状态意味着多线程并发情况下如果是单个实例就容易出现线程安全问题。
//
//            在如今的普遍分布式多线程环境中，你就不得不每次一个请求就创建一个状态机实例。但问题来了一旦碰到某些状态机它的构建过程很复杂，如果当下QPS又很高话，往往会造成系统的性能瓶颈。
//
//            推荐下阿里开源的状态机：cola-statemachine。支持状态机到持久化和状态机到分布式部署

            stateMachine = orderStateMachineFactory.getStateMachine(stateMachineId);

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
            if (stateMachine != null) {
                stateMachine.stop();
            }
        }
        return false;
    }
}
