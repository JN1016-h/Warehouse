package com.ai.algorithm;

/**
 * 动销算法（FR3）
 */
public final class SellThroughCalculator {

    private SellThroughCalculator() {
    }

    public static double coefficient(double outboundQty, double avgStock) {
        double denom = avgStock <= 0 ? 1.0 : avgStock;
        return outboundQty / denom;
    }

    public static String level(double coeff) {
        if (coeff > 1.2) {
            return "畅销";
        }
        if (coeff >= 0.4) {
            return "平销";
        }
        return "滞销";
    }

    public static double sellThroughRate(int skusWithOutbound, int totalSkuCount) {
        if (totalSkuCount <= 0) {
            return 0.0;
        }
        return skusWithOutbound * 100.0 / totalSkuCount;
    }
}
