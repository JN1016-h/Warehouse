package com.controller;

import com.dto.*;
import com.enums.PaymentStatus;
import com.service.FinanceService;
import com.service.ExportService;
import com.utils.PageUtils;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * FinanceController测试类
 * 测试财务管理相关接口
 */
public class FinanceControllerTest {
    
    @Mock
    private FinanceService financeService;
    
    @Mock
    private ExportService exportService;
    
    @InjectMocks
    private FinanceController financeController;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testQueryReceivables_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");
        
        List<ReceivableDTO> list = new ArrayList<>();
        ReceivableDTO dto = new ReceivableDTO();
        dto.setId(1L);
        dto.setOrderNo("OUT001");
        dto.setCustomerName("客户A");
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentStatus(PaymentStatus.UNPAID);
        list.add(dto);
        
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        
        // 模拟服务层返回
        when(financeService.queryReceivables(any(ReceivableQuery.class))).thenReturn(pageUtils);
        
        // 调用控制器方法
        R result = financeController.queryReceivables(params);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }
    
    @Test
    public void testQueryPayables_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");
        
        List<PayableDTO> list = new ArrayList<>();
        PayableDTO dto = new PayableDTO();
        dto.setId(1L);
        dto.setOrderNo("IN001");
        dto.setSupplierName("供应商A");
        dto.setAmount(new BigDecimal("2000.00"));
        dto.setPaymentStatus(PaymentStatus.PAID);
        list.add(dto);
        
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        
        // 模拟服务层返回
        when(financeService.queryPayables(any(PayableQuery.class))).thenReturn(pageUtils);
        
        // 调用控制器方法
        R result = financeController.queryPayables(params);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }
    
    @Test
    public void testUpdatePaymentStatus_Success() {
        // 准备测试数据
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(1L);
        dto.setOrderType("OUTBOUND");
        dto.setPaymentStatus(PaymentStatus.PAID);
        
        // 模拟服务层返回
        when(financeService.updatePaymentStatus(1L, "OUTBOUND", PaymentStatus.PAID)).thenReturn(true);
        
        // 调用控制器方法
        R result = financeController.updatePaymentStatus(dto);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertEquals("付款状态更新成功", result.get("msg"));
    }
    
    @Test
    public void testUpdatePaymentStatus_OrderNotFound() {
        // 准备测试数据
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(999L);
        dto.setOrderType("OUTBOUND");
        dto.setPaymentStatus(PaymentStatus.PAID);
        
        // 模拟服务层返回
        when(financeService.updatePaymentStatus(999L, "OUTBOUND", PaymentStatus.PAID)).thenReturn(false);
        
        // 调用控制器方法
        R result = financeController.updatePaymentStatus(dto);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("付款状态更新失败，订单不存在", result.get("msg"));
    }
    
    @Test
    public void testUpdatePaymentStatus_NullOrderId() {
        // 准备测试数据
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderType("OUTBOUND");
        dto.setPaymentStatus(PaymentStatus.PAID);
        
        // 调用控制器方法
        R result = financeController.updatePaymentStatus(dto);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("订单ID不能为空", result.get("msg"));
    }
    
    @Test
    public void testGetReceivableSummary_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        
        FinanceSummary summary = new FinanceSummary();
        summary.setTotalAmount(new BigDecimal("10000.00"));
        summary.setPaidAmount(new BigDecimal("6000.00"));
        summary.setUnpaidAmount(new BigDecimal("4000.00"));
        summary.setTotalCount(10);
        summary.setPaidCount(6);
        summary.setUnpaidCount(4);
        
        // 模拟服务层返回
        when(financeService.calculateReceivableSummary(any(ReceivableQuery.class))).thenReturn(summary);
        
        // 调用控制器方法
        R result = financeController.getReceivableSummary(params);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
        FinanceSummary returnedSummary = (FinanceSummary) result.get("data");
        assertEquals(new BigDecimal("10000.00"), returnedSummary.getTotalAmount());
    }
    
    @Test
    public void testGetPayableSummary_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        
        FinanceSummary summary = new FinanceSummary();
        summary.setTotalAmount(new BigDecimal("20000.00"));
        summary.setPaidAmount(new BigDecimal("15000.00"));
        summary.setUnpaidAmount(new BigDecimal("5000.00"));
        summary.setTotalCount(20);
        summary.setPaidCount(15);
        summary.setUnpaidCount(5);
        
        // 模拟服务层返回
        when(financeService.calculatePayableSummary(any(PayableQuery.class))).thenReturn(summary);
        
        // 调用控制器方法
        R result = financeController.getPayableSummary(params);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
        FinanceSummary returnedSummary = (FinanceSummary) result.get("data");
        assertEquals(new BigDecimal("20000.00"), returnedSummary.getTotalAmount());
    }
}
