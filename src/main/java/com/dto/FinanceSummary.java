package com.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 财务汇总数据传输对象
 * 用于在前后端之间传输财务统计汇总信息，包含总金额、已付款金额、未付款金额及对应的记录数
 * 
 * 验证需求: 9.1, 9.2, 9.3, 9.4
 */
public class FinanceSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 已付款金额
     */
    private BigDecimal paidAmount;
    
    /**
     * 未付款金额
     */
    private BigDecimal unpaidAmount;
    
    /**
     * 总记录数
     */
    private Integer totalCount;
    
    /**
     * 已付款记录数
     */
    private Integer paidCount;
    
    /**
     * 未付款记录数
     */
    private Integer unpaidCount;
    
    /**
     * 默认构造函数
     * 初始化所有金额为0，所有计数为0
     */
    public FinanceSummary() {
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.unpaidAmount = BigDecimal.ZERO;
        this.totalCount = 0;
        this.paidCount = 0;
        this.unpaidCount = 0;
    }
    
    /**
     * 全参数构造函数
     * 
     * @param totalAmount 总金额
     * @param paidAmount 已付款金额
     * @param unpaidAmount 未付款金额
     * @param totalCount 总记录数
     * @param paidCount 已付款记录数
     * @param unpaidCount 未付款记录数
     */
    public FinanceSummary(BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal unpaidAmount,
                         Integer totalCount, Integer paidCount, Integer unpaidCount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.paidAmount = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        this.unpaidAmount = unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO;
        this.totalCount = totalCount != null ? totalCount : 0;
        this.paidCount = paidCount != null ? paidCount : 0;
        this.unpaidCount = unpaidCount != null ? unpaidCount : 0;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount != null ? paidAmount : BigDecimal.ZERO;
    }
    
    public BigDecimal getUnpaidAmount() {
        return unpaidAmount;
    }
    
    public void setUnpaidAmount(BigDecimal unpaidAmount) {
        this.unpaidAmount = unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount != null ? totalCount : 0;
    }
    
    public Integer getPaidCount() {
        return paidCount;
    }
    
    public void setPaidCount(Integer paidCount) {
        this.paidCount = paidCount != null ? paidCount : 0;
    }
    
    public Integer getUnpaidCount() {
        return unpaidCount;
    }
    
    public void setUnpaidCount(Integer unpaidCount) {
        this.unpaidCount = unpaidCount != null ? unpaidCount : 0;
    }
    
    @Override
    public String toString() {
        return "FinanceSummary{" +
                "totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", unpaidAmount=" + unpaidAmount +
                ", totalCount=" + totalCount +
                ", paidCount=" + paidCount +
                ", unpaidCount=" + unpaidCount +
                '}';
    }
}
