package com.tineco.market.client.dto.response.activity.calculatePrice;

import com.tineco.market.client.constants.ActivityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品计算价格结果
 */
@Data
public class CalculatePriceResponse {

    @ApiModelProperty("活动ID")
    private Long activityId;

    @ApiModelProperty("参与活动的商品总价: 如满折活动：商品原价 * 参加活动的商品数量 * 折扣")
    private BigDecimal activityPrice;

    @ApiModelProperty("参与活动的商品单价: 如满折活动：商品原价 * 折扣。没有参加任何活动这里就是原价")
    private BigDecimal activityGoodsPrice;

    @ApiModelProperty("参与活动的商品数量")
    private Integer activityQuantity;

    @ApiModelProperty("未参与活动的商品数量")
    private Integer notJoinActivityQuantity;

    @ApiModelProperty("差价（参加活动的商品原总价 - 参与活动的商品总价）")
    private BigDecimal priceDifference;

    /**
     * 活动类型 1-满折活动
     *
     * @see ActivityType
     */
    @ApiModelProperty("活动类型 0-不参加活动，1-满折活动")
    private Integer activityType;

    @ApiModelProperty("活动详情描述,如：该商品满两件8折，三件5折")
    private String activityDesc;
}
