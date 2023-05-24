package org.project.order.service.order;

import org.project.order.dto.OrderProcessDataDto;
import org.project.order.vo.SaveOrderVo;

import java.util.List;

/**
 * @author wangcheng
 * date 2022/11/25
 */
public interface OrderProcessChain {

    /**
     * 开始处理
     *
     * @param saveOrderVo 原始保存订单数据
     * @param orderList   订单处理数据
     */
    void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList);
}
