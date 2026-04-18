package com.dto;

import com.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 应付款项数据传输对象
 * 用于在前后端之间传输应付款项信息，包含入库订单的付款相关信息
 */
public class PayableDTO implements Serializable {
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
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 应付金额
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
    
    public PayableDTO() {
    }
    
    public PayableDTO(Long id, String orderNo, String supplierName, BigDecimal amount,
                     PaymentStatus paymentStatus, Date orderDate, Date paymentTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.supplierName = supplierName;
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
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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
        return "PayableDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paymentStatusDisplayName='" + paymentStatusDisplayName + '\'' +
                ", orderDate=" + orderDate +
                ", paymentTime=" + paymentTime +
                '}';
    }
}
