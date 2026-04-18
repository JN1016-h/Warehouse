package com.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AmountFormatter单元测试
 * 测试金额格式化功能
 */
public class AmountFormatterTest {
    
    /**
     * 测试formatAmount(BigDecimal) - 正常金额
     * 验证正常金额能够正确格式化为千分位两位小数
     */
    @Test
    public void testFormatAmount_BigDecimal_Normal() {
        assertEquals("1,234.56", AmountFormatter.formatAmount(new BigDecimal("1234.56")));
        assertEquals("1,000.00", AmountFormatter.formatAmount(new BigDecimal("1000")));
        assertEquals("999.99", AmountFormatter.formatAmount(new BigDecimal("999.99")));
        assertEquals("10,000.50", AmountFormatter.formatAmount(new BigDecimal("10000.50")));
    }
    
    /**
     * 测试formatAmount(BigDecimal) - 大金额
     * 验证大金额能够正确格式化
     */
    @Test
    public void testFormatAmount_BigDecimal_LargeAmount() {
        assertEquals("1,000,000.00", AmountFormatter.formatAmount(new BigDecimal("1000000")));
        assertEquals("123,456,789.12", AmountFormatter.formatAmount(new BigDecimal("123456789.12")));
        assertEquals("999,999,999.99", AmountFormatter.formatAmount(new BigDecimal("999999999.99")));
    }
    
    /**
     * 测试formatAmount(BigDecimal) - 零值
     * 验证零值能够正确格式化
     */
    @Test
    public void testFormatAmount_BigDecimal_Zero() {
        assertEquals("0.00", AmountFormatter.formatAmount(new BigDecimal("0")));
        assertEquals("0.00", AmountFormatter.formatAmount(BigDecimal.ZERO));
    }
    
    /**
     * 测试formatAmount(BigDecimal) - null值
     * 验证null值返回"0.00"
     */
    @Test
    public void testFormatAmount_BigDecimal_Null() {
        assertEquals("0.00", AmountFormatter.formatAmount((BigDecimal) null));
    }
    
    /**
     * 测试formatAmount(BigDecimal) - 小数位数处理
     * 验证不同小数位数能够正确格式化为两位小数
     */
    @Test
    public void testFormatAmount_BigDecimal_DecimalPlaces() {
        // 一位小数
        assertEquals("100.50", AmountFormatter.formatAmount(new BigDecimal("100.5")));
        
        // 三位小数（四舍五入）
        assertEquals("100.13", AmountFormatter.formatAmount(new BigDecimal("100.125")));
        assertEquals("100.13", AmountFormatter.formatAmount(new BigDecimal("100.126")));
        
        // 无小数
        assertEquals("100.00", AmountFormatter.formatAmount(new BigDecimal("100")));
    }
    
    /**
     * 测试formatAmount(BigDecimal) - 负数
     * 验证负数能够正确格式化
     */
    @Test
    public void testFormatAmount_BigDecimal_Negative() {
        assertEquals("-1,234.56", AmountFormatter.formatAmount(new BigDecimal("-1234.56")));
        assertEquals("-100.00", AmountFormatter.formatAmount(new BigDecimal("-100")));
    }
    
    /**
     * 测试formatAmount(Double) - 正常金额
     * 验证Double类型金额能够正确格式化
     */
    @Test
    public void testFormatAmount_Double_Normal() {
        assertEquals("1,234.56", AmountFormatter.formatAmount(1234.56));
        assertEquals("1,000.00", AmountFormatter.formatAmount(1000.0));
        assertEquals("999.99", AmountFormatter.formatAmount(999.99));
    }
    
    /**
     * 测试formatAmount(Double) - null值
     * 验证null值返回"0.00"
     */
    @Test
    public void testFormatAmount_Double_Null() {
        assertEquals("0.00", AmountFormatter.formatAmount((Double) null));
    }
    
    /**
     * 测试formatAmount(Double) - 零值
     * 验证零值能够正确格式化
     */
    @Test
    public void testFormatAmount_Double_Zero() {
        assertEquals("0.00", AmountFormatter.formatAmount(0.0));
    }
    
    /**
     * 测试formatAmount(Long) - 正常金额
     * 验证Long类型金额能够正确格式化
     */
    @Test
    public void testFormatAmount_Long_Normal() {
        assertEquals("1,234.00", AmountFormatter.formatAmount(1234L));
        assertEquals("1,000.00", AmountFormatter.formatAmount(1000L));
        assertEquals("999.00", AmountFormatter.formatAmount(999L));
    }
    
    /**
     * 测试formatAmount(Long) - null值
     * 验证null值返回"0.00"
     */
    @Test
    public void testFormatAmount_Long_Null() {
        assertEquals("0.00", AmountFormatter.formatAmount((Long) null));
    }
    
    /**
     * 测试formatAmount(Long) - 零值
     * 验证零值能够正确格式化
     */
    @Test
    public void testFormatAmount_Long_Zero() {
        assertEquals("0.00", AmountFormatter.formatAmount(0L));
    }
    
    /**
     * 测试formatAmount(Integer) - 正常金额
     * 验证Integer类型金额能够正确格式化
     */
    @Test
    public void testFormatAmount_Integer_Normal() {
        assertEquals("1,234.00", AmountFormatter.formatAmount(1234));
        assertEquals("1,000.00", AmountFormatter.formatAmount(1000));
        assertEquals("999.00", AmountFormatter.formatAmount(999));
    }
    
    /**
     * 测试formatAmount(Integer) - null值
     * 验证null值返回"0.00"
     */
    @Test
    public void testFormatAmount_Integer_Null() {
        assertEquals("0.00", AmountFormatter.formatAmount((Integer) null));
    }
    
    /**
     * 测试formatAmount(Integer) - 零值
     * 验证零值能够正确格式化
     */
    @Test
    public void testFormatAmount_Integer_Zero() {
        assertEquals("0.00", AmountFormatter.formatAmount(0));
    }
    
    /**
     * 测试formatAmount - 边界值
     * 验证各种边界值能够正确格式化
     */
    @Test
    public void testFormatAmount_BoundaryValues() {
        // 最小正数
        assertEquals("0.01", AmountFormatter.formatAmount(new BigDecimal("0.01")));
        
        // 接近千位的数
        assertEquals("999.99", AmountFormatter.formatAmount(new BigDecimal("999.99")));
        assertEquals("1,000.00", AmountFormatter.formatAmount(new BigDecimal("1000.00")));
        assertEquals("1,000.01", AmountFormatter.formatAmount(new BigDecimal("1000.01")));
        
        // 接近百万的数
        assertEquals("999,999.99", AmountFormatter.formatAmount(new BigDecimal("999999.99")));
        assertEquals("1,000,000.00", AmountFormatter.formatAmount(new BigDecimal("1000000.00")));
    }
    
    /**
     * 测试formatAmount - 精度测试
     * 验证格式化保持两位小数精度
     */
    @Test
    public void testFormatAmount_Precision() {
        // 测试四舍五入
        assertEquals("1.23", AmountFormatter.formatAmount(new BigDecimal("1.234")));
        assertEquals("1.24", AmountFormatter.formatAmount(new BigDecimal("1.235")));
        assertEquals("1.24", AmountFormatter.formatAmount(new BigDecimal("1.236")));
        
        // 测试补零
        assertEquals("1.00", AmountFormatter.formatAmount(new BigDecimal("1")));
        assertEquals("1.10", AmountFormatter.formatAmount(new BigDecimal("1.1")));
    }
    
    /**
     * 测试formatAmount - 千分位分隔符
     * 验证千分位分隔符正确添加
     */
    @Test
    public void testFormatAmount_ThousandsSeparator() {
        // 无千分位
        assertEquals("999.00", AmountFormatter.formatAmount(new BigDecimal("999")));
        
        // 一个千分位
        assertEquals("1,000.00", AmountFormatter.formatAmount(new BigDecimal("1000")));
        assertEquals("9,999.00", AmountFormatter.formatAmount(new BigDecimal("9999")));
        
        // 两个千分位
        assertEquals("10,000.00", AmountFormatter.formatAmount(new BigDecimal("10000")));
        assertEquals("99,999.00", AmountFormatter.formatAmount(new BigDecimal("99999")));
        
        // 三个千分位
        assertEquals("100,000.00", AmountFormatter.formatAmount(new BigDecimal("100000")));
        assertEquals("999,999.00", AmountFormatter.formatAmount(new BigDecimal("999999")));
        
        // 四个千分位
        assertEquals("1,000,000.00", AmountFormatter.formatAmount(new BigDecimal("1000000")));
    }
}
