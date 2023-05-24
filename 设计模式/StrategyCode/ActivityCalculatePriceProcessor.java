package com.tineco.market.application.service.activity.calculatePrice;

import com.alibaba.fastjson.JSONObject;
import com.tineco.market.client.dto.request.activity.calculatePrice.CalculatePriceRequest;
import com.tineco.market.client.dto.response.activity.calculatePrice.CalculatePriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 活动商品计算价格
 */
@Slf4j
@Service
public class ActivityCalculatePriceProcessor implements InitializingBean {

    private List<ActivityGoodsCalculatePriceStrategy> strategies;

    @Autowired
    private ApplicationContext applicationContext;

    public ActivityCalculatePriceProcessor() {
        this.strategies = new ArrayList<>();
    }

    public void addStrategy(ActivityGoodsCalculatePriceStrategy strategy) {
        this.strategies.add(strategy);
    }

    /**
     * 获取所有的计算价格的策略
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ActivityGoodsCalculatePriceStrategy> beansOfType = applicationContext.getBeansOfType(ActivityGoodsCalculatePriceStrategy.class);
        for (ActivityGoodsCalculatePriceStrategy activityGoodsCalculatePriceStrategy : beansOfType.values()) {
            addStrategy(activityGoodsCalculatePriceStrategy);
        }
    }

    /**
     * 计算最低价格
     * @param calculatePriceRequest 商品原价及数量等信息
     * @return CalculatePriceResponse
     */
    public CalculatePriceResponse calculatePrice(CalculatePriceRequest calculatePriceRequest) {
        CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
        BigDecimal minPrice = BigDecimal.valueOf(Long.MAX_VALUE);
        for (ActivityGoodsCalculatePriceStrategy strategy : strategies) {
            CalculatePriceResponse calculatePriceResponseByStrategy= strategy.calculatePrice(calculatePriceRequest);
            if (calculatePriceResponseByStrategy.getActivityPrice().compareTo(minPrice) < 0) {
                minPrice = calculatePriceResponseByStrategy.getActivityPrice();
                calculatePriceResponse.setActivityPrice(minPrice);
                calculatePriceResponse.setActivityQuantity(calculatePriceResponseByStrategy.getActivityQuantity());
                calculatePriceResponse.setNotJoinActivityQuantity(calculatePriceResponseByStrategy.getNotJoinActivityQuantity());
                calculatePriceResponse.setActivityType(calculatePriceResponseByStrategy.getActivityType());
                calculatePriceResponse.setPriceDifference(calculatePriceResponseByStrategy.getPriceDifference());
                calculatePriceResponse.setActivityDesc(calculatePriceResponseByStrategy.getActivityDesc());
                calculatePriceResponse.setActivityId(calculatePriceResponseByStrategy.getActivityId());
                calculatePriceResponse.setActivityGoodsPrice(calculatePriceResponseByStrategy.getActivityGoodsPrice());
            }
        }
        log.info("calculatePrice Result ：{}", JSONObject.toJSONString(calculatePriceResponse));
        return calculatePriceResponse;
    }
}
