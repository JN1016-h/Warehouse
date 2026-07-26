package com.dto;

import com.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 应收款项查询条件对象
 * 用于封装应收款项的查询条件，包括日期范围、付款状态、客户名称和分页参数
 */
public class ReceivableQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 开始日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    
    /**
     * 结束日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    
    /**
     * 付款状态（可选，为null表示查询所有状态）
     */
    private PaymentStatus paymentStatus;
    
    /**
     * 客户名称（模糊查询）
     */
    private String customerName;
    
    /**
     * 页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer limit;
    
    public ReceivableQuery() {
    }
    
    public ReceivableQuery(Date startDate, Date endDate, PaymentStatus paymentStatus,
                          String customerName, Integer page, Integer limit) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentStatus = paymentStatus;
        this.customerName = customerName;
        this.page = page;
        this.limit = limit;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    @Override
    public String toString() {
        return "ReceivableQuery{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", paymentStatus=" + paymentStatus +
                ", customerName='" + customerName + '\'' +
                ", page=" + page +
                ", limit=" + limit +
                '}';
    }
}
