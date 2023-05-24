package com.tineco.market.client.dto.request.activity.calculatePrice;

import com.tineco.market.client.constants.ActivityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 计算商品价格请求数据
 */
@Data
public class CalculatePriceRequest {

    @ApiModelProperty("goods_common表id(SPU)")
    @NotNull
    private Long goodsCommonId;

    @ApiModelProperty("goods_base表id(SKU)")
    @NotNull
    private Long goodsBaseId;

    @ApiModelProperty("商品原价（正常价格）")
    @NotNull
    private BigDecimal originalPrice;

    @ApiModelProperty("商品数量")
    @NotNull
    private Integer quantity;

    /**
     * 活动类型 1-满折活动
     *
     * @see ActivityType
     */
    @ApiModelProperty("指定活动类型，如传入1代表只按满折活动来计算活动总价，即使商品参加其他活动且活动总价比满折活动低，依然按满折活动计算")
    @NotNull
    private Integer activityType;
}
