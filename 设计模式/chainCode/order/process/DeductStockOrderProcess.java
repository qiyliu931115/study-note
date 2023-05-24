package org.project.order.service.order.process;

import lombok.extern.slf4j.Slf4j;
import matrix.boot.based.utils.WebUtil;
import matrix.boot.common.exception.I18nBusinessException;
import matrix.boot.common.exception.ServiceException;
import org.project.basic.client.utils.FeignResultUtil;
import org.project.goods.client.dto.CombinationGoodsForOrderDto;
import org.project.goods.client.feign.GoodsStockClient;
import org.project.goods.client.vo.OrderIncrOrDeductStockVo;
import org.project.order.dto.OrderGoodsProcessDataDto;
import org.project.order.dto.OrderProcessDataDto;
import org.project.order.service.order.OrderProcess;
import org.project.order.service.order.OrderProcessChain;
import org.project.order.vo.SaveOrderVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 扣减商品库存
 *
 * @author wangcheng
 * date 2022/11/25
 */
@Slf4j
public class DeductStockOrderProcess implements OrderProcess {

    @Override
    public void doProcess(SaveOrderVo saveOrderVo, List<OrderProcessDataDto> orderList, OrderProcessChain chain) {
        //获取商品库存客户端
        GoodsStockClient goodsStockClient = WebUtil.getBean(GoodsStockClient.class);
        try {
            orderList.forEach(orderProcessData -> {
                //组装参数
                List<OrderIncrOrDeductStockVo> params = new ArrayList<>();
                //定义商品项字典项（key -> goodsId）
                Map<String, OrderIncrOrDeductStockVo> deductStockMap = new HashMap<>();
                //组合商品列表数据
                List<OrderGoodsProcessDataDto> combinationGoodsList = new ArrayList<>();
                orderProcessData.getOrderGoodsProcessDataList().forEach(item -> {
                    OrderIncrOrDeductStockVo deductStockVo = new OrderIncrOrDeductStockVo().setGoodsId(item.getGoodsId()).setGoodsNum(item.getGoodsCount());
                    params.add(deductStockVo);
                    deductStockMap.put(item.getGoodsId(), deductStockVo);
                    if (Boolean.TRUE.equals(item.getCombination()) && !CollectionUtils.isEmpty(item.getCombinationGoodsList())) {
                        combinationGoodsList.add(item);
                    }
                });
                if (!CollectionUtils.isEmpty(combinationGoodsList)) {
                    //处理组合商品逻辑
                    combinationGoodsList.forEach(orderGoodsProcessDataDto ->
                            orderGoodsProcessDataDto.getCombinationGoodsList().forEach(combinationGoodsForOrder -> {
                                if (deductStockMap.containsKey(combinationGoodsForOrder.getSonGoodsId())) {
                                    //存在组合商品中子项
                                    OrderIncrOrDeductStockVo stockVo = deductStockMap.get(combinationGoodsForOrder.getSonGoodsId());
                                    stockVo.setGoodsNum(stockVo.getGoodsNum() + (combinationGoodsForOrder.getSonGoodsNum() * orderGoodsProcessDataDto.getGoodsCount()));
                                } else {
                                    //不存在组合商品中子项
                                    OrderIncrOrDeductStockVo deductStockVo = new OrderIncrOrDeductStockVo().setGoodsId(combinationGoodsForOrder.getSonGoodsId())
                                            .setGoodsNum(combinationGoodsForOrder.getSonGoodsNum() * orderGoodsProcessDataDto.getGoodsCount());
                                    params.add(deductStockVo);
                                    deductStockMap.put(combinationGoodsForOrder.getSonGoodsId(), deductStockVo);
                                }
                            })
                    );
                }
                //扣减库存
                FeignResultUtil.getData(goodsStockClient.stockDeductByOrder(orderProcessData.getOrderId(), params));
            });
            chain.doProcess(saveOrderVo, orderList);
        } catch (Exception e) {
            //尝试实时还原库存
            orderList.forEach(orderProcessData -> FeignResultUtil.getData(goodsStockClient.restoreStockByOrder(orderProcessData.getOrderId())));
            throw e;
        }
    }

}
