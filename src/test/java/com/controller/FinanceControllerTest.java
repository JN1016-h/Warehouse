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
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    public void testExportReceivablesToExcel() throws Exception {
        List<ReceivableDTO> list = Collections.singletonList(new ReceivableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryReceivables(any(ReceivableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("EXCEL"))).thenReturn(true);
        when(exportService.exportToExcel(any(), any(), any())).thenReturn(new byte[]{1, 2});

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsByteArray().length > 0);
    }

    @Test
    public void testExportReceivablesToPdf() throws Exception {
        List<ReceivableDTO> list = Collections.singletonList(new ReceivableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryReceivables(any(ReceivableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("PDF"))).thenReturn(true);
        when(exportService.exportToPDF(any(), anyString(), any(), any())).thenReturn(new byte[]{3, 4});

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testExportPayablesToExcel() throws Exception {
        List<PayableDTO> list = Collections.singletonList(new PayableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryPayables(any(PayableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("EXCEL"))).thenReturn(true);
        when(exportService.exportToExcel(any(), any(), any())).thenReturn(new byte[]{5, 6});

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testExportPayablesToPdf() throws Exception {
        List<PayableDTO> list = Collections.singletonList(new PayableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryPayables(any(PayableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("PDF"))).thenReturn(true);
        when(exportService.exportToPDF(any(), anyString(), any(), any())).thenReturn(new byte[]{7, 8});

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testExportReceivablesTooLarge() throws Exception {
        List<ReceivableDTO> list = Collections.singletonList(new ReceivableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryReceivables(any(ReceivableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("EXCEL"))).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testUpdatePaymentStatus_NullOrderType() {
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(1L);
        dto.setPaymentStatus(PaymentStatus.PAID);

        R result = financeController.updatePaymentStatus(dto);
        assertEquals(500, result.get("code"));
        assertEquals("订单类型不能为空", result.get("msg"));
    }

    @Test
    public void testUpdatePaymentStatus_NullPaymentStatus() {
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(1L);
        dto.setOrderType("OUTBOUND");

        R result = financeController.updatePaymentStatus(dto);
        assertEquals(500, result.get("code"));
        assertEquals("付款状态不能为空", result.get("msg"));
    }

    @Test
    public void testUpdatePaymentStatus_EmptyOrderType() {
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(1L);
        dto.setOrderType("");
        dto.setPaymentStatus(PaymentStatus.PAID);

        R result = financeController.updatePaymentStatus(dto);
        assertEquals(500, result.get("code"));
        assertEquals("订单类型不能为空", result.get("msg"));
    }

    @Test
    public void testQueryReceivables_WithAllParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "2");
        params.put("limit", "20");
        params.put("startDate", "2025-01-01");
        params.put("endDate", "2025-12-31");
        params.put("paymentStatus", "PAID");
        params.put("customerName", "客户A");

        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 20, 1));

        R result = financeController.queryReceivables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQueryReceivables_InvalidDateAndStatus() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", "bad-date");
        params.put("endDate", "also-bad");
        params.put("paymentStatus", "NOT_A_STATUS");

        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryReceivables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQueryReceivables_DefaultPagination() {
        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryReceivables(new HashMap<String, Object>());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQueryPayables_WithAllParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "5");
        params.put("startDate", "2025-06-01");
        params.put("endDate", "2025-06-30");
        params.put("paymentStatus", "UNPAID");
        params.put("supplierName", "供应商X");

        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 5, 1));

        R result = financeController.queryPayables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetReceivableSummary_WithFilters() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", "2025-01-01");
        params.put("paymentStatus", "UNPAID");
        params.put("customerName", "客户B");

        FinanceSummary summary = new FinanceSummary();
        when(financeService.calculateReceivableSummary(any(ReceivableQuery.class))).thenReturn(summary);

        R result = financeController.getReceivableSummary(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetPayableSummary_WithFilters() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("endDate", "2025-12-31");
        params.put("supplierName", "供应商Y");

        FinanceSummary summary = new FinanceSummary();
        when(financeService.calculatePayableSummary(any(PayableQuery.class))).thenReturn(summary);

        R result = financeController.getPayableSummary(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testExportPayablesTooLargeExcel() throws Exception {
        List<PayableDTO> list = Collections.singletonList(new PayableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryPayables(any(PayableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("EXCEL"))).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testExportReceivablesPdfTooLarge() throws Exception {
        List<ReceivableDTO> list = Collections.singletonList(new ReceivableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryReceivables(any(ReceivableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("PDF"))).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testExportPayablesPdfTooLarge() throws Exception {
        List<PayableDTO> list = Collections.singletonList(new PayableDTO());
        PageUtils pageUtils = new PageUtils(list, 1, 10, 1);
        when(financeService.queryPayables(any(PayableQuery.class))).thenReturn(pageUtils);
        when(exportService.validateExportSize(anyInt(), eq("PDF"))).thenReturn(false);

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testUpdatePaymentStatus_ServiceException() {
        PaymentUpdateDTO dto = new PaymentUpdateDTO();
        dto.setOrderId(1L);
        dto.setOrderType("OUTBOUND");
        dto.setPaymentStatus(PaymentStatus.PAID);

        when(financeService.updatePaymentStatus(1L, "OUTBOUND", PaymentStatus.PAID))
                .thenThrow(new RuntimeException("db error"));

        R result = financeController.updatePaymentStatus(dto);
        assertEquals(500, result.get("code"));
        assertTrue(String.valueOf(result.get("msg")).contains("db error"));
    }

    @Test
    public void testExportReceivablesExcelServiceError() throws Exception {
        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenThrow(new RuntimeException("query failed"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void testExportPayablesPdfServiceError() throws Exception {
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenThrow(new RuntimeException("pdf query failed"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void testQueryReceivablesServiceError() {
        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenThrow(new RuntimeException("list error"));

        R result = financeController.queryReceivables(new HashMap<String, Object>());
        assertEquals(500, result.get("code"));
        assertTrue(String.valueOf(result.get("msg")).contains("list error"));
    }

    @Test
    public void testGetPayableSummaryServiceError() {
        when(financeService.calculatePayableSummary(any(PayableQuery.class)))
                .thenThrow(new RuntimeException("summary error"));

        R result = financeController.getPayableSummary(new HashMap<String, Object>());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testGetReceivableSummaryServiceError() {
        when(financeService.calculateReceivableSummary(any(ReceivableQuery.class)))
                .thenThrow(new RuntimeException("recv summary error"));

        R result = financeController.getReceivableSummary(new HashMap<String, Object>());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testQueryPayablesServiceError() {
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenThrow(new RuntimeException("payables error"));

        R result = financeController.queryPayables(new HashMap<String, Object>());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testQueryReceivablesWithEmptyPaymentStatus() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paymentStatus", "");
        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryReceivables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQueryPayablesDefaultPagination() {
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryPayables(new HashMap<String, Object>());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testExportPayablesExcelServiceError() throws Exception {
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenThrow(new RuntimeException("excel error"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportPayablesToExcel(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void testQueryPayablesWithEmptyPaymentStatus() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paymentStatus", "");
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryPayables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQueryPayablesInvalidDateAndStatus() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", "bad");
        params.put("paymentStatus", "INVALID");
        when(financeService.queryPayables(any(PayableQuery.class)))
                .thenReturn(new PageUtils(Collections.emptyList(), 0, 10, 1));

        R result = financeController.queryPayables(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetReceivableSummaryInvalidDates() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", "not-a-date");
        params.put("endDate", "also-bad");
        when(financeService.calculateReceivableSummary(any(ReceivableQuery.class)))
                .thenReturn(new FinanceSummary());

        R result = financeController.getReceivableSummary(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetPayableSummaryInvalidDates() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", "bad");
        when(financeService.calculatePayableSummary(any(PayableQuery.class)))
                .thenReturn(new FinanceSummary());

        R result = financeController.getPayableSummary(params);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testExportReceivablesPdfServiceError() throws Exception {
        when(financeService.queryReceivables(any(ReceivableQuery.class)))
                .thenThrow(new RuntimeException("pdf error"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        financeController.exportReceivablesToPDF(new HashMap<String, Object>(), response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }
}
