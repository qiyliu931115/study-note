package com.tineco.market.application.service.activity.calculatePrice;

import com.alibaba.fastjson.JSONObject;
import com.tineco.market.client.constants.ActivityType;
import com.tineco.market.client.dto.request.activity.calculatePrice.CalculatePriceRequest;
import com.tineco.market.client.dto.response.activity.calculatePrice.CalculatePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 不参与任何活动商品的计算价格策略
 */
@Slf4j
@Service
public class AbstractActivityGoodsCalculatePriceStrategy implements ActivityGoodsCalculatePriceStrategy {


    private static final ActivityType activityType = ActivityType.NORMAL;
    /**
     * 该商品任何活动都没参加
     *
     * @param calculatePriceRequest
     * @return
     */
    @Override
    public CalculatePriceResponse calculatePrice(CalculatePriceRequest calculatePriceRequest) {
        BigDecimal originalPrice = calculatePriceRequest.getOriginalPrice();
        Integer quantity = calculatePriceRequest.getQuantity();
        CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
        BigDecimal originalTotalPrice = originalPrice.multiply(BigDecimal.valueOf(quantity));
        calculatePriceResponse.setActivityPrice(originalTotalPrice);
        calculatePriceResponse.setActivityQuantity(0);
        calculatePriceResponse.setNotJoinActivityQuantity(quantity);
        calculatePriceResponse.setActivityType(activityType.getCode());
        //差价 （商品总价 - 参与活动的商品总价） 无活动参加 差价是0
        calculatePriceResponse.setPriceDifference(BigDecimal.ZERO);
        //参加活动后每个商品的单价 这里没有参加任何活动就是原价
        calculatePriceResponse.setActivityGoodsPrice(originalPrice);
        //不参加任何活动 设置为0
        calculatePriceResponse.setActivityId(0L);
        log.info("AbstractActivityGoodsCalculatePriceStrategy calculatePrice Result ：{}", JSONObject.toJSONString(calculatePriceResponse));
        return calculatePriceResponse;
    }

    @Override
    public ActivityType getActivityType() {
        return activityType;
    }
}
