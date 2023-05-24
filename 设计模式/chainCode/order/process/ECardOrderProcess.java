package org.project.order.service.order.process;

import org.project.order.dto.OrderProcessDataDto;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderVo;

import java.util.List;

/**
 * E卡处理
 *
 * @author wangcheng
 * 2022/11/30
 **/
public class ECardOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        //todo E卡处理
        chain.doProcess(saveOrderVo, orderList);
    }
}
