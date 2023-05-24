package org.project.order.service.order;

import org.project.order.dto.OrderProcessDataDto;
import org.project.order.vo.SaveOrderVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 订单处理持有者
 *
 * @author wangcheng
 * date 2022/11/25
 */
@Component
public class OrderProcessHandler implements InitializingBean {

    /**
     * 订单处理链条
     */
    private OrderProcessChain orderProcessChain;

    /**
     * 开始下单
     *
     * @param orderList 订单处理数据
     */
    public void doOrder(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList) {
        orderProcessChain.doProcess(saveOrderVo, orderList);
    }

    /**
     * 默认订单处理链条
     */
    private static class DefaultOrderProcessChain implements OrderProcessChain {

        /**
         * 索引字段
         */
        private final int index;

        /**
         * 订单处理类列表
         */
        private final List<OrderProcess> orderProcessList;

        DefaultOrderProcessChain(List<OrderProcess> processList) {
            this.orderProcessList = processList;
            this.index = 0;
        }

        private DefaultOrderProcessChain(DefaultOrderProcessChain parent, int index) {
            this.orderProcessList = parent.getOrderProcessList();
            this.index = index;
        }

        public List<OrderProcess> getOrderProcessList() {
            return orderProcessList;
        }

        @Override
        public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList) {
            if (this.index < orderProcessList.size()) {
                OrderProcess orderProcess = orderProcessList.get(this.index);
                DefaultOrderProcessChain chain = new DefaultOrderProcessChain(this, this.index + 1);
                orderProcess.doProcess(saveOrderVo, orderList, chain);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceLoader<OrderProcess> serviceLoader = ServiceLoader.load(OrderProcess.class);
        List<OrderProcess> processList = new ArrayList<>();
        for (OrderProcess orderProcess : serviceLoader) {
            processList.add(orderProcess);
        }
        if (CollectionUtils.isEmpty(processList)) {
            return;
        }
        //组装链条
        orderProcessChain = new DefaultOrderProcessChain(processList);
    }
}
