package com.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额格式化工具类
 */
public class AmountFormatter {
    
    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.00");
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额
     * @return 格式化后的字符串，例如：1,234.56
     */
    public static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return AMOUNT_FORMAT.format(amount);
    }
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额（double类型）
     * @return 格式化后的字符串，例如：1,234.56
     */
    public static String formatAmount(double amount) {
        return AMOUNT_FORMAT.format(amount);
    }
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额（long类型）
     * @return 格式化后的字符串，例如：1,234.00
     */
    public static String formatAmount(long amount) {
        return AMOUNT_FORMAT.format(amount);
    }
}
