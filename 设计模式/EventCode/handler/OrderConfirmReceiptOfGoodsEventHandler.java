package com.yf.ShoppingCart.web.event.handler;

import cn.hutool.core.lang.UUID;
import com.yf.ShoppingCart.web.constants.CRMChangeType;
import com.yf.ShoppingCart.web.constants.MemberTaskTypeEnum;
import com.yf.ShoppingCart.web.dao.OrderBaseDao;
import com.yf.ShoppingCart.web.dto.OrderConfirmReceiptOfGoodsEventDTO;
import com.yf.ShoppingCart.web.event.dto.MemberTaskDTO;
import com.yf.ShoppingCart.web.event.member.OrderConfirmReceiptOfGoodsEventEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单确认收货事件处理
 */
@Component
@Slf4j
public class OrderConfirmReceiptOfGoodsEventHandler extends AbstractEventHandler<OrderConfirmReceiptOfGoodsEventEvent>  {

    @Autowired
    private OrderBaseDao orderBaseDao;

    @Override
    public void handleEvent(OrderConfirmReceiptOfGoodsEventEvent event) {
        String orderId = event.getOrderId();
        List<OrderConfirmReceiptOfGoodsEventDTO> orderConfirmReceiptOfGoodsEventDTOS = orderBaseDao.getByOrderId(orderId);

        if (CollectionUtils.isEmpty(orderConfirmReceiptOfGoodsEventDTOS)) {
            log.warn("OrderConfirmReceiptOfGoodsEventHandler Failure : Not Find Confirm Receipt Order :{}", orderId);
            return;
        }

        String userId = orderConfirmReceiptOfGoodsEventDTOS.get(0).getUserId();

        String userCenterUserId = tkService.getUserCenterUserId(userId);
        if (StringUtils.isBlank(userCenterUserId)) {
            log.error("OrderConfirmReceiptOfGoodsEventHandler Failure, userCenterUserId Don't Exist, userId :{}", userId);
            return;
        }

        List<MemberTaskDTO> memberTaskDTOS = new ArrayList<>(orderConfirmReceiptOfGoodsEventDTOS.size());

        String uuid = UUID.randomUUID().toString();
        for (OrderConfirmReceiptOfGoodsEventDTO orderConfirmReceiptOfGoodsEventDTO : orderConfirmReceiptOfGoodsEventDTOS) {
            //商品数量
            Integer orderGoodsNum = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsNum();
            //商品实付金额
            BigDecimal orderGoodsPaymentAmount = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsPaymentAmount();
            //每个商品平均的实付金额
            BigDecimal divide = orderGoodsPaymentAmount.divide(BigDecimal.valueOf(orderGoodsNum), 2, RoundingMode.HALF_UP);
            //假如一个订单内只有一个商品， 该商品数量为2件 需要组装2个MemberTaskDTO 发送每件商品的实付金额
            for (int i = 0 ; i < orderGoodsNum; i ++) {
                MemberTaskDTO memberTaskDTO = new MemberTaskDTO();
                memberTaskDTO.setUserId(orderConfirmReceiptOfGoodsEventDTO.getUserId());
                memberTaskDTO.setOrderId(orderConfirmReceiptOfGoodsEventDTO.getOrderId());
                memberTaskDTO.setChangeType(CRMChangeType.ADD.code());
                memberTaskDTO.setTaskId(uuid);
                memberTaskDTO.setUserId(userCenterUserId);
                memberTaskDTO.setChangeNum(divide);
                String catName = orderConfirmReceiptOfGoodsEventDTO.getCatName();
                if (catName.contains(MemberTaskTypeEnum.SMART)) {
                    memberTaskDTO.setTaskType(MemberTaskTypeEnum.ORDER_MACHINE.code());
                } else {
                    memberTaskDTO.setTaskType(MemberTaskTypeEnum.ORDER_ACCESSORY.code());
                }
                memberTaskDTOS.add(memberTaskDTO);
            }
        }

        tkService.sendMsgToCRM(memberTaskDTOS);
    }
}
