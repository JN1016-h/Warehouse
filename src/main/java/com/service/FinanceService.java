package com.service;

import com.dto.FinanceSummary;
import com.dto.PayableDTO;
import com.dto.PayableQuery;
import com.dto.ReceivableDTO;
import com.dto.ReceivableQuery;
import com.enums.PaymentStatus;
import com.utils.PageUtils;

/**
 * 财务服务接口
 * 
 * @author 
 * @email 
 * @date 2025-01-02
 */
public interface FinanceService {
    
    /**
     * 查询应收款项列表
     * @param query 查询条件
     * @return 分页结果
     */
    PageUtils queryReceivables(ReceivableQuery query);
    
    /**
     * 查询应付款项列表
     * @param query 查询条件
     * @return 分页结果
     */
    PageUtils queryPayables(PayableQuery query);
    
    /**
     * 更新付款状态
     * @param orderId 订单ID
     * @param orderType 订单类型：INBOUND-入库, OUTBOUND-出库
     * @param status 新的付款状态
     * @return 是否更新成功
     */
    boolean updatePaymentStatus(Long orderId, String orderType, PaymentStatus status);
    
    /**
     * 统计应收款项金额
     * @param query 查询条件
     * @return 财务汇总信息
     */
    FinanceSummary calculateReceivableSummary(ReceivableQuery query);
    
    /**
     * 统计应付款项金额
     * @param query 查询条件
     * @return 财务汇总信息
     */
    FinanceSummary calculatePayableSummary(PayableQuery query);
}
