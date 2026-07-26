package com.ai.algorithm;

/**
 * 库龄积压 / 缺货风险（FR6）
 */
public final class RiskCalculator {

    private RiskCalculator() {
    }

    public static boolean overstock(int currentStock, double outboundInWindow, double slowThreshold) {
        return currentStock > 0 && outboundInWindow <= slowThreshold;
    }

    public static int ageDays(Long lastOutboundMs, Long lastInboundMs, long nowMs, long windowDays) {
        Long ref = lastOutboundMs != null ? lastOutboundMs : lastInboundMs;
        if (ref == null) {
            return (int) windowDays;
        }
        long days = (nowMs - ref) / (24L * 60L * 60L * 1000L);
        return (int) Math.max(days, 0);
    }

    public static boolean stockoutRisk(int currentStock, Integer threshold, double daily, int leadDays) {
        if (threshold != null && currentStock <= threshold) {
            return true;
        }
        if (daily > 0 && currentStock / daily < leadDays) {
            return true;
        }
        return false;
    }

    public static double supportDays(int currentStock, double daily) {
        if (daily <= 0) {
            return currentStock > 0 ? Double.POSITIVE_INFINITY : 0;
        }
        return currentStock / daily;
    }
}
