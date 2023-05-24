package com.tineco.market.application.service.activity.calculatePrice;

import com.tineco.market.client.constants.ActivityType;
import com.tineco.market.client.dto.request.activity.calculatePrice.CalculatePriceRequest;
import com.tineco.market.client.dto.response.activity.calculatePrice.CalculatePriceResponse;

import java.math.BigDecimal;

/**
 * 活动商品计算价格
 */
public interface ActivityGoodsCalculatePriceStrategy {

    /**
     * 根据活动来计算商品价格
     * @param calculatePriceRequest 商品原价及数量等信息
     * @return CalculatePriceResponse
     */
    CalculatePriceResponse calculatePrice(CalculatePriceRequest calculatePriceRequest);

    /**
     * 获取活动类型
     * @return ActivityType
     */
    ActivityType getActivityType();
}
