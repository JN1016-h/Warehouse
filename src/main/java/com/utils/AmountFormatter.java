package com.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.math.RoundingMode;

/**
 * 金额格式化工具类
 */
public class AmountFormatter {
    
    private static final String AMOUNT_PATTERN = "#,##0.00";
    private static final ThreadLocal<DecimalFormat> AMOUNT_FORMAT =
            ThreadLocal.withInitial(() -> {
                DecimalFormat df = new DecimalFormat(AMOUNT_PATTERN);
                df.setRoundingMode(RoundingMode.HALF_UP);
                return df;
            });

    private static String formatScaled(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return AMOUNT_FORMAT.get().format(amount.setScale(2, RoundingMode.HALF_UP));
    }
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额
     * @return 格式化后的字符串，例如：1,234.56
     */
    public static String formatAmount(BigDecimal amount) {
        return formatScaled(amount);
    }
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额（double类型）
     * @return 格式化后的字符串，例如：1,234.56
     */
    public static String formatAmount(double amount) {
        return formatScaled(BigDecimal.valueOf(amount));
    }

    public static String formatAmount(Double amount) {
        return amount == null ? "0.00" : formatAmount(amount.doubleValue());
    }
    
    /**
     * 格式化金额为千分位两位小数
     * 
     * @param amount 金额（long类型）
     * @return 格式化后的字符串，例如：1,234.00
     */
    public static String formatAmount(long amount) {
        return formatScaled(BigDecimal.valueOf(amount));
    }

    public static String formatAmount(Long amount) {
        return amount == null ? "0.00" : formatAmount(amount.longValue());
    }

    public static String formatAmount(Integer amount) {
        return amount == null ? "0.00" : formatScaled(BigDecimal.valueOf(amount.longValue()));
    }
}
