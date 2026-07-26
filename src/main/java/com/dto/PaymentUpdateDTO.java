package com.dto;

import com.enums.PaymentStatus;

import java.io.Serializable;

/**
 * 付款状态更新数据传输对象
 * 用于更新订单的付款状态，支持入库订单和出库订单
 */
public class PaymentUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单类型：INBOUND-入库订单, OUTBOUND-出库订单
     */
    private String orderType;
    
    /**
     * 新的付款状态
     */
    private PaymentStatus paymentStatus;
    
    public PaymentUpdateDTO() {
    }
    
    public PaymentUpdateDTO(Long orderId, String orderType, PaymentStatus paymentStatus) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.paymentStatus = paymentStatus;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    @Override
    public String toString() {
        return "PaymentUpdateDTO{" +
                "orderId=" + orderId +
                ", orderType='" + orderType + '\'' +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
