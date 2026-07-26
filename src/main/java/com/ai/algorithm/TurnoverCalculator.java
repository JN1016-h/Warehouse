package com.ai.algorithm;

/**
 * 周转算法（FR5）
 */
public final class TurnoverCalculator {

    private TurnoverCalculator() {
    }

    public static double turnoverRate(double outboundQty, double avgStock) {
        double denom = avgStock <= 0 ? 1.0 : avgStock;
        return outboundQty / denom;
    }

    public static double turnoverDays(double turnoverRate, long windowDays) {
        if (turnoverRate <= 0) {
            return windowDays;
        }
        return windowDays / turnoverRate;
    }

    /**
     * 资金占用估算 = 当前库存 × 参考单价（销售单价近似）
     */
    public static double capitalOccupation(int currentStock, double unitPrice) {
        if (currentStock <= 0 || unitPrice <= 0) {
            return 0.0;
        }
        return currentStock * unitPrice;
    }
}
