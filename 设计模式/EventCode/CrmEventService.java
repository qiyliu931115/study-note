package com.yf.ShoppingCart.web.services.tk;

import com.yf.ShoppingCart.web.constants.CRMChangeType;
import com.yf.ShoppingCart.web.constants.CRMOrderStatus;
import com.yf.ShoppingCart.web.constants.MemberTaskTypeEnum;
import com.yf.ShoppingCart.web.constants.RefundOrderStatusEnum;
import com.yf.ShoppingCart.web.dao.OrderBaseDao;
import com.yf.ShoppingCart.web.dao.customerDao.ReturnMoneyDao;
import com.yf.ShoppingCart.web.dto.OrderConfirmReceiptOfGoodsEventDTO;
import com.yf.ShoppingCart.web.entity.ReturnOrder;
import com.yf.ShoppingCart.web.entity.tk.CRMReturnOrderBO;
import com.yf.ShoppingCart.web.entity.tk.CRMOrderResponse;
import com.yf.ShoppingCart.web.event.member.EvaluateAccessoryEvent;
import com.yf.ShoppingCart.web.event.member.EvaluationMachineEvent;
import com.yf.ShoppingCart.web.event.member.OrderConfirmReceiptOfGoodsEventEvent;
import com.yf.ShoppingCart.web.util.ResultConstants;
import com.yf.ShoppingCart.web.util.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CrmEventService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ReturnMoneyDao returnMoneyDao;

    @Autowired
    private OrderBaseDao orderBaseDao;

    @Autowired
    private TkService tkService;

    /**
     * 订单确认收货后发送消息给CRM
     * @param orderId 订单ID
     */
    public void sendOrderConfirmReceiptOfGoodsEvent(String orderId) {
        OrderConfirmReceiptOfGoodsEventEvent orderConfirmReceiptOfGoodsEventEvent = new OrderConfirmReceiptOfGoodsEventEvent(this);
        orderConfirmReceiptOfGoodsEventEvent.setOrderId(orderId);
        applicationEventPublisher.publishEvent(orderConfirmReceiptOfGoodsEventEvent);
    }


    /**
     * 评价大件商品后发送消息给CRM
     * @param token   用户token
     * @param orderId 订单ID
     * @param goodsBaseId goodsBaseId
     * @param orderGoodsPaymentAmount 订单商品实付金额
     */
    public void sendEvaluationMachineEvent(String token, String orderId, Integer goodsBaseId, BigDecimal orderGoodsPaymentAmount) {
        EvaluationMachineEvent evaluationMachineEvent = new EvaluationMachineEvent(this);
        evaluationMachineEvent.setToken(token);
        evaluationMachineEvent.setGoodsId(goodsBaseId);
        evaluationMachineEvent.setOrderId(orderId);
        evaluationMachineEvent.setOrderGoodsPaymentAmount(orderGoodsPaymentAmount);
        applicationEventPublisher.publishEvent(evaluationMachineEvent);
    }

    /**
     * 评价配件商品后发送消息给CRM
     *
     * @param token
     */
    public void sendEvaluateAccessoryEvent(String token, String orderId, Integer goodsBaseId, BigDecimal orderGoodsPaymentAmount) {
        EvaluateAccessoryEvent evaluateAccessoryEvent = new EvaluateAccessoryEvent(this);
        evaluateAccessoryEvent.setToken(token);
        evaluateAccessoryEvent.setGoodsId(goodsBaseId);
        evaluateAccessoryEvent.setOrderId(orderId);
        evaluateAccessoryEvent.setOrderGoodsPaymentAmount(orderGoodsPaymentAmount);
        applicationEventPublisher.publishEvent(evaluateAccessoryEvent);
    }

    /**
     * CRM系统查询订单信息
     * @param orderId
     */
    public CRMOrderResponse getOrderById(String orderId) {
        //根据订单获取订单商品总价格（不包含运费）
        BigDecimal orderGoodsAmount = returnMoneyDao.orderExists(orderId);
        if (orderGoodsAmount == null) {
            throw new BizException(ResultConstants.ORDER_DOES_NOT_EXIST);
        }

        CRMOrderResponse crmOrderResponse = new CRMOrderResponse();
        crmOrderResponse.setOrderTotalPrice(orderGoodsAmount);
        crmOrderResponse.setOrderId(orderId);
        List<CRMReturnOrderBO> crmOrderBOS = returnMoneyDao.getOrderStatus(orderId);
        if (CollectionUtils.isEmpty(crmOrderBOS)) {
            //没有退货单
            crmOrderResponse.setOrderStatus(CRMOrderStatus.CONFIRM_RECEIPT.code());
        } else {
            //有退货单 先默认都是流程结束
            crmOrderResponse.setOrderStatus(CRMOrderStatus.CONFIRM_RECEIPT.code());
            //遍历所有退货单
            for (CRMReturnOrderBO crmOrderBO : crmOrderBOS) {
                if (crmOrderBO.getRefundStatus() != null) {
                    CRMOrderStatus crmOrderStatus = getCRMOrderStatus(crmOrderBO.getRefundStatus());
                    //如果退货单有一个在处理中 结束循环
                    if (crmOrderStatus.equals(CRMOrderStatus.AFTER_SALE_PROCESSING)) {
                        crmOrderResponse.setOrderStatus(CRMOrderStatus.AFTER_SALE_PROCESSING.code());
                        break;
                    }
                }
            }
        }

        //如果有正在处理的退货单
        if (crmOrderResponse.getOrderStatus().equals(CRMOrderStatus.AFTER_SALE_PROCESSING.code())) {
            crmOrderResponse.setOrderDetailList(inProcessOrderDetailList(orderId));
            return crmOrderResponse;
        }
        //没有正在处理的退货单
        crmOrderResponse.setOrderDetailList(confirmReceiptOrderDetailList(orderId, crmOrderBOS));


        return crmOrderResponse;
    }

    private List<CRMOrderResponse.OrderDetail> confirmReceiptOrderDetailList(String orderId, List<CRMReturnOrderBO> crmOrderBOS) {

        List<OrderConfirmReceiptOfGoodsEventDTO> orderConfirmReceiptOfGoodsEventDTOS = orderBaseDao.getByOrderId(orderId);

        if (CollectionUtils.isEmpty(orderConfirmReceiptOfGoodsEventDTOS)) {
            log.error("CrmEventService confirmReceiptOrderDetailList Failure : Not Find Confirm Receipt Order :{}", orderId);
            throw new BizException(ResultConstants.ORDER_DOES_NOT_EXIST);
        }

        List<CRMOrderResponse.OrderDetail> orderDetailList = new ArrayList<>(orderConfirmReceiptOfGoodsEventDTOS.size());

        for (OrderConfirmReceiptOfGoodsEventDTO orderConfirmReceiptOfGoodsEventDTO : orderConfirmReceiptOfGoodsEventDTOS) {
            Long goodsId = orderConfirmReceiptOfGoodsEventDTO.getGoodsId();

            List<ReturnOrder> returnOrders = returnMoneyDao.getByOrderIdAndGoodsId(goodsId, orderId);
            //退款商品数量
            long refundGoodsNum = returnOrders.stream().collect(Collectors.summarizingInt(ReturnOrder::getOrder_goods_num)).getSum();

            //订单中单个商品的数量
            Integer orderGoodsNum = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsNum();

            if (orderGoodsNum < refundGoodsNum) {
                log.error("CrmEventService confirmReceiptOrderDetailList Failure : refundGoodsNum: {} orderGoodsNum: {}", refundGoodsNum, orderGoodsNum);
                throw new BizException(ResultConstants.REFUND_GOODS_NUM_ERROR);
            }
            //商品实付金额
            BigDecimal orderGoodsPaymentAmount = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsPaymentAmount();
            //每个商品平均的实付金额
            BigDecimal divide = orderGoodsPaymentAmount.divide(BigDecimal.valueOf(orderGoodsNum), 2, RoundingMode.HALF_UP);
            //假如一个订单内只有一个商品， 该商品数量为2件 需要组装2个orderDetail 发送每件商品的实付金额
            for (int i = 0 ; i < orderGoodsNum; i ++) {
                CRMOrderResponse.OrderDetail orderDetail = new CRMOrderResponse.OrderDetail();
                orderDetail.setChangeType(CRMChangeType.ADD.code());
                orderDetail.setChangeNum(divide);
                String catName = orderConfirmReceiptOfGoodsEventDTO.getCatName();
                if (catName.contains(MemberTaskTypeEnum.SMART)) {
                    orderDetail.setTaskType(MemberTaskTypeEnum.ORDER_MACHINE.code());
                } else {
                    orderDetail.setTaskType(MemberTaskTypeEnum.ORDER_ACCESSORY.code());
                }


                for (int j = crmOrderBOS.size() - 1; j >= 0; j--) {
                    CRMReturnOrderBO crmReturnOrderBO = crmOrderBOS.get(j);
                    if (crmReturnOrderBO.getGoodsId().equals(orderConfirmReceiptOfGoodsEventDTO.getGoodsId())) {
                        Integer refundStatus = crmReturnOrderBO.getRefundStatus();
                        if (Objects.equals(refundStatus, RefundOrderStatusEnum.REFUND_SUCCESSFUL.code())) {
                            orderDetail.setTaskType(MemberTaskTypeEnum.AFTER_SALES_ORDER.code());
                            orderDetail.setChangeType(CRMChangeType.SUB.code());
                            crmReturnOrderBO.setOrderGoodsNum(crmReturnOrderBO.getOrderGoodsNum().subtract(BigDecimal.ONE));
                            if (crmReturnOrderBO.getOrderGoodsNum().equals(BigDecimal.ZERO) ) {
                                crmOrderBOS.remove(j);
                            }
                            break;
                        }
                    }
                }
                orderDetailList.add(orderDetail);
            }

        }

        return orderDetailList;
    }

    private List<CRMOrderResponse.OrderDetail> inProcessOrderDetailList(String orderId) {

        List<OrderConfirmReceiptOfGoodsEventDTO> orderConfirmReceiptOfGoodsEventDTOS = orderBaseDao.getByOrderId(orderId);

        if (CollectionUtils.isEmpty(orderConfirmReceiptOfGoodsEventDTOS)) {
            log.error("CrmEventService inProcessOrderDetailList Failure : Not Find Confirm Receipt Order :{}", orderId);
            throw new BizException(ResultConstants.ORDER_DOES_NOT_EXIST);
        }

        List<CRMOrderResponse.OrderDetail> orderDetailList = new ArrayList<>(orderConfirmReceiptOfGoodsEventDTOS.size());

        for (OrderConfirmReceiptOfGoodsEventDTO orderConfirmReceiptOfGoodsEventDTO : orderConfirmReceiptOfGoodsEventDTOS) {
            //商品数量
            Integer orderGoodsNum = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsNum();
            //商品实付金额
            BigDecimal orderGoodsPaymentAmount = orderConfirmReceiptOfGoodsEventDTO.getOrderGoodsPaymentAmount();
            //每个商品平均的实付金额
            BigDecimal divide = orderGoodsPaymentAmount.divide(BigDecimal.valueOf(orderGoodsNum), 2, RoundingMode.HALF_UP);
            //假如一个订单内只有一个商品， 该商品数量为2件 需要组装2个orderDetail 发送每件商品的实付金额
            for (int i = 0 ; i < orderGoodsNum; i ++) {
                CRMOrderResponse.OrderDetail orderDetail = new CRMOrderResponse.OrderDetail();
                orderDetail.setChangeType(CRMChangeType.ADD.code());
                orderDetail.setChangeNum(divide);
                String catName = orderConfirmReceiptOfGoodsEventDTO.getCatName();
                if (catName.contains(MemberTaskTypeEnum.SMART)) {
                    orderDetail.setTaskType(MemberTaskTypeEnum.ORDER_MACHINE.code());
                } else {
                    orderDetail.setTaskType(MemberTaskTypeEnum.ORDER_ACCESSORY.code());
                }
                orderDetailList.add(orderDetail);
            }
        }
        return orderDetailList;
    }

    /**
     *
     * @param refundStatus
     * @return
     */
    private CRMOrderStatus getCRMOrderStatus(Integer refundStatus) {
        RefundOrderStatusEnum byCode = RefundOrderStatusEnum.getByCode(refundStatus);
        switch (byCode) {
            case REFUSE:
            case REJECTED:
            case CANCEL:
            case REFUND_SUCCESSFUL:
                //没有正在处理的售后(售后已完成或者没有售后)
                return CRMOrderStatus.CONFIRM_RECEIPT;
            case UNDER_REVIEW:
            case APPROVED:
            case RECEIVED:
            case FILL_IN_LOGISTICS:
            case MIDDLEWARE_REVIEW:
                //售后处理中
                return CRMOrderStatus.AFTER_SALE_PROCESSING;
            default:
                throw new BizException(ResultConstants.REFUND_ORDER_STATUS_ERROR);
        }

    }
}
