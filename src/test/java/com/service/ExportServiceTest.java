package com.service;

import com.service.impl.ExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExportService单元测试
 */
public class ExportServiceTest {
    
    private ExportService exportService;
    
    @BeforeEach
    public void setUp() {
        exportService = new ExportServiceImpl();
    }
    
    /**
     * 测试数据DTO
     */
    static class TestData {
        private String orderNo;
        private String name;
        private BigDecimal amount;
        private String status;
        private Date orderDate;
        
        public TestData(String orderNo, String name, BigDecimal amount, String status, Date orderDate) {
            this.orderNo = orderNo;
            this.name = name;
            this.amount = amount;
            this.status = status;
            this.orderDate = orderDate;
        }
        
        public String getOrderNo() { return orderNo; }
        public String getName() { return name; }
        public BigDecimal getAmount() { return amount; }
        public String getStatus() { return status; }
        public Date getOrderDate() { return orderDate; }
    }
    
    @Test
    public void testExportToExcel_WithValidData_ShouldGenerateExcel() {
        // 准备测试数据
        List<TestData> data = new ArrayList<>();
        data.add(new TestData("ORD001", "客户A", new BigDecimal("1000.50"), "已付款", new Date()));
        data.add(new TestData("ORD002", "客户B", new BigDecimal("2500.00"), "未付款", new Date()));
        
        String[] headers = {"订单号", "客户名称", "金额", "付款状态", "订单日期"};
        String[] fields = {"orderNo", "name", "amount", "status", "orderDate"};
        
        // 执行导出
        byte[] result = exportService.exportToExcel(data, headers, fields);
        
        // 验证结果
        assertNotNull(result, "Excel文件不应为null");
        assertTrue(result.length > 0, "Excel文件应包含数据");
    }
    
    @Test
    public void testExportToExcel_WithEmptyData_ShouldGenerateExcelWithHeadersOnly() {
        // 准备空数据
        List<TestData> data = new ArrayList<>();
        String[] headers = {"订单号", "客户名称", "金额"};
        String[] fields = {"orderNo", "name", "amount"};
        
        // 执行导出
        byte[] result = exportService.exportToExcel(data, headers, fields);
        
        // 验证结果
        assertNotNull(result, "Excel文件不应为null");
        assertTrue(result.length > 0, "Excel文件应包含表头");
    }
    
    @Test
    public void testExportToPDF_WithValidData_ShouldGeneratePDF() {
        // 准备测试数据
        List<TestData> data = new ArrayList<>();
        data.add(new TestData("ORD001", "客户A", new BigDecimal("1000.50"), "已付款", new Date()));
        data.add(new TestData("ORD002", "客户B", new BigDecimal("2500.00"), "未付款", new Date()));
        
        String title = "应收款项报表";
        String[] headers = {"订单号", "客户名称", "金额", "付款状态", "订单日期"};
        String[] fields = {"orderNo", "name", "amount", "status", "orderDate"};
        
        // 执行导出
        byte[] result = exportService.exportToPDF(data, title, headers, fields);
        
        // 验证结果
        assertNotNull(result, "PDF文件不应为null");
        assertTrue(result.length > 0, "PDF文件应包含数据");
    }
    
    @Test
    public void testExportToPDF_WithEmptyData_ShouldGeneratePDFWithHeadersOnly() {
        // 准备空数据
        List<TestData> data = new ArrayList<>();
        String title = "应收款项报表";
        String[] headers = {"订单号", "客户名称", "金额"};
        String[] fields = {"orderNo", "name", "amount"};
        
        // 执行导出
        byte[] result = exportService.exportToPDF(data, title, headers, fields);
        
        // 验证结果
        assertNotNull(result, "PDF文件不应为null");
        assertTrue(result.length > 0, "PDF文件应包含标题和表头");
    }
    
    @Test
    public void testValidateExportSize_ExcelWithinLimit_ShouldReturnTrue() {
        // 测试Excel在限制内
        boolean result = exportService.validateExportSize(5000, "EXCEL");
        assertTrue(result, "5000条记录应该通过Excel验证");
    }
    
    @Test
    public void testValidateExportSize_ExcelExceedsLimit_ShouldReturnFalse() {
        // 测试Excel超出限制
        boolean result = exportService.validateExportSize(15000, "EXCEL");
        assertFalse(result, "15000条记录应该不通过Excel验证");
    }
    
    @Test
    public void testValidateExportSize_ExcelAtLimit_ShouldReturnTrue() {
        // 测试Excel刚好在限制边界
        boolean result = exportService.validateExportSize(10000, "EXCEL");
        assertTrue(result, "10000条记录应该通过Excel验证");
    }
    
    @Test
    public void testValidateExportSize_PDFWithinLimit_ShouldReturnTrue() {
        // 测试PDF在限制内
        boolean result = exportService.validateExportSize(500, "PDF");
        assertTrue(result, "500条记录应该通过PDF验证");
    }
    
    @Test
    public void testValidateExportSize_PDFExceedsLimit_ShouldReturnFalse() {
        // 测试PDF超出限制
        boolean result = exportService.validateExportSize(1500, "PDF");
        assertFalse(result, "1500条记录应该不通过PDF验证");
    }
    
    @Test
    public void testValidateExportSize_PDFAtLimit_ShouldReturnTrue() {
        // 测试PDF刚好在限制边界
        boolean result = exportService.validateExportSize(1000, "PDF");
        assertTrue(result, "1000条记录应该通过PDF验证");
    }
    
    @Test
    public void testValidateExportSize_InvalidType_ShouldReturnFalse() {
        // 测试无效的导出类型
        boolean result = exportService.validateExportSize(100, "INVALID");
        assertFalse(result, "无效的导出类型应该返回false");
    }
    
    @Test
    public void testValidateExportSize_CaseInsensitive_ShouldWork() {
        // 测试大小写不敏感
        assertTrue(exportService.validateExportSize(100, "excel"), "小写excel应该有效");
        assertTrue(exportService.validateExportSize(100, "pdf"), "小写pdf应该有效");
        assertTrue(exportService.validateExportSize(100, "Excel"), "混合大小写Excel应该有效");
        assertTrue(exportService.validateExportSize(100, "Pdf"), "混合大小写Pdf应该有效");
    }
}
