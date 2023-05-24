package com.tineco.market.application.service.activity.calculatePrice;

import com.alibaba.fastjson.JSONObject;
import com.tineco.market.application.entity.activity.discount.DiscountActivityGoodsInfo;
import com.tineco.market.application.mapper.activity.discount.DiscountActivityGoodsMapper;
import com.tineco.market.client.constants.ActivityType;
import com.tineco.market.client.dto.request.activity.calculatePrice.CalculatePriceRequest;
import com.tineco.market.client.dto.response.activity.calculatePrice.CalculatePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 参与满折活动商品的计算价格策略
 */
@Slf4j
@Service
public class DiscountActivityStrategy extends AbstractActivityGoodsCalculatePriceStrategy implements ActivityGoodsCalculatePriceStrategy {


    @Autowired
    private DiscountActivityGoodsMapper discountActivityGoodsMapper;

    private static final ActivityType activityType = ActivityType.DISCOUNT;
    @Override
    public CalculatePriceResponse calculatePrice(CalculatePriceRequest calculatePriceRequest) {

        try {
            CalculatePriceRequest calculatePriceRequest1 = calculatePriceRequest;
            Long goodsBaseId = calculatePriceRequest1.getGoodsBaseId();
            Integer quantity = calculatePriceRequest1.getQuantity();
            BigDecimal originalPrice = calculatePriceRequest1.getOriginalPrice();
            List<DiscountActivityGoodsInfo> discountActivityGoodsInfoList = discountActivityGoodsMapper.getByGoodsBaseId(goodsBaseId, quantity, new Date());
            if (CollectionUtils.isEmpty(discountActivityGoodsInfoList)) {
                return super.calculatePrice(calculatePriceRequest);
            }
            //取第一条最接近quantity的数据
            DiscountActivityGoodsInfo discountActivityGoodsInfo = discountActivityGoodsInfoList.get(0);
            CalculatePriceResponse calculatePriceResponse = getCalculatePriceResponse(discountActivityGoodsInfo, originalPrice, quantity);

            log.info("DiscountActivityStrategy calculatePrice Result ：{}", JSONObject.toJSONString(calculatePriceResponse));
            return calculatePriceResponse;
        } catch (Exception e) {
            //如果满折活动出现错误 使用默认的计算价格策略
            log.error("DiscountActivityStrategy calculatePrice Error : {}", ExceptionUtils.getStackTrace(e));
            return super.calculatePrice(calculatePriceRequest);
        }

    }

    @Override
    public ActivityType getActivityType() {
        return activityType;
    }

    /**
     * 组装活动数据
     *
     * @param discountActivityGoodsInfo
     * @param originalPrice
     * @param quantity
     * @return
     */
    private CalculatePriceResponse getCalculatePriceResponse(DiscountActivityGoodsInfo discountActivityGoodsInfo, BigDecimal originalPrice, Integer quantity) {
        //获取折扣 如7.00折 要将小数点左移1位
        //Integer conditionAmount = discountActivityGoodsInfo.getConditionAmount();
        BigDecimal discount = discountActivityGoodsInfo.getDiscount().movePointLeft(1);

        //参加活动的商品原总价：(商品原价* 数量)
        BigDecimal originalTotalPrice = originalPrice.multiply(BigDecimal.valueOf(quantity));

        CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
        //参与活动的商品总价: (商品原价 * 商品数量 * 折扣) 四舍五入保留两位小数
        BigDecimal activityPrice = originalPrice.multiply(BigDecimal.valueOf(quantity)).multiply(discount).setScale(2, RoundingMode.HALF_UP);
        calculatePriceResponse.setActivityPrice(activityPrice);
        //参与活动的商品数量
        calculatePriceResponse.setActivityQuantity(quantity);
        //未参加活动的商品数量
        calculatePriceResponse.setNotJoinActivityQuantity(0);
        calculatePriceResponse.setActivityType(activityType.getCode());
        //差价 （参加活动的商品原总价 - 参与活动的商品总价）
        calculatePriceResponse.setPriceDifference(originalTotalPrice.subtract(activityPrice));
        //参加活动后每个商品的单价
        calculatePriceResponse.setActivityGoodsPrice(originalPrice.multiply(discount));

        calculatePriceResponse.setActivityId(discountActivityGoodsInfo.getActivityId());

        return calculatePriceResponse;
    }
}
