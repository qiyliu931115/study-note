package com.yf.ShoppingCart.web.event.dto;

import com.yf.ShoppingCart.web.constants.MemberTaskTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员任务事件
 */
@Data
public class MemberTaskDTO {

    /**
     * 用户中心 用户ID
     */
    private String userId;

    /**
     * 任务类型
     * @see MemberTaskTypeEnum
     */
    private String taskType;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 改变类型 0-增加积分 1-减少积分 @see CRMChangeType
     */
    private Integer changeType;


    /**
     * 商品金额
     */
    private BigDecimal changeNum;


}

