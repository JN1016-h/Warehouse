package com.dto;

import com.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 应收款项数据传输对象
 * 用于在前后端之间传输应收款项信息，包含出库订单的收款相关信息
 */
public class ReceivableDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 应收金额
     */
    private BigDecimal amount;
    
    /**
     * 付款状态枚举
     */
    private PaymentStatus paymentStatus;
    
    /**
     * 付款状态显示名称（中文）
     */
    private String paymentStatusDisplayName;
    
    /**
     * 订单日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date orderDate;
    
    /**
     * 付款时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;
    
    public ReceivableDTO() {
    }
    
    public ReceivableDTO(Long id, String orderNo, String customerName, BigDecimal amount,
                        PaymentStatus paymentStatus, Date orderDate, Date paymentTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        if (paymentStatus != null) {
            this.paymentStatusDisplayName = paymentStatus.getDisplayName();
        }
        this.orderDate = orderDate;
        this.paymentTime = paymentTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        // 自动设置付款状态显示名称
        if (paymentStatus != null) {
            this.paymentStatusDisplayName = paymentStatus.getDisplayName();
        }
    }
    
    public String getPaymentStatusDisplayName() {
        return paymentStatusDisplayName;
    }
    
    public void setPaymentStatusDisplayName(String paymentStatusDisplayName) {
        this.paymentStatusDisplayName = paymentStatusDisplayName;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public Date getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    @Override
    public String toString() {
        return "ReceivableDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paymentStatusDisplayName='" + paymentStatusDisplayName + '\'' +
                ", orderDate=" + orderDate +
                ", paymentTime=" + paymentTime +
                '}';
    }
}
