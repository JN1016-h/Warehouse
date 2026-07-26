package com.ai.algorithm;

/**
 * 补货算法（FR4）
 */
public final class ReplenishCalculator {

    private ReplenishCalculator() {
    }

    public static int resolveLeadDays(Integer skuLeadDays, int defaultLeadDays) {
        if (skuLeadDays != null && skuLeadDays > 0) {
            return skuLeadDays;
        }
        return defaultLeadDays > 0 ? defaultLeadDays : 14;
    }

    public static double dailyConsumption(double outboundLast30Days) {
        return outboundLast30Days / 30.0;
    }

    /**
     * 建议补货数量 = max(0, 日均消耗 × 采购周期 − 当前库存)
     * 当前库存 ≤ 阈值时强制进入补货提醒
     */
    public static int suggestQty(double daily, int leadDays, int currentStock, Integer threshold) {
        int suggest = (int) Math.ceil(Math.max(0, daily * leadDays - currentStock));
        boolean force = threshold != null && currentStock <= threshold;
        if (force && suggest == 0) {
            int need = threshold - currentStock;
            suggest = Math.max(need, 1);
        }
        return suggest;
    }

    public static boolean forceAlert(int currentStock, Integer threshold) {
        return threshold != null && currentStock <= threshold;
    }
}
