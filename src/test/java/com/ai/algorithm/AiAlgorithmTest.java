package com.ai.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AiAlgorithmTest {

    @Test
    public void sellThroughLevels() {
        assertEquals("畅销", SellThroughCalculator.level(1.21));
        assertEquals("平销", SellThroughCalculator.level(0.4));
        assertEquals("平销", SellThroughCalculator.level(1.2));
        assertEquals("滞销", SellThroughCalculator.level(0.39));
        assertEquals(50.0, SellThroughCalculator.sellThroughRate(5, 10), 0.001);
        assertEquals(2.0, SellThroughCalculator.coefficient(20, 10), 0.001);
    }

    @Test
    public void replenishFormula() {
        assertEquals(14, ReplenishCalculator.resolveLeadDays(null, 14));
        assertEquals(7, ReplenishCalculator.resolveLeadDays(7, 14));
        // daily=2, lead=14 => need 28, stock=10 => suggest 18
        assertEquals(18, ReplenishCalculator.suggestQty(2.0, 14, 10, 20));
        // force when below threshold and suggest would be 0
        assertEquals(5, ReplenishCalculator.suggestQty(0.0, 14, 5, 10));
        assertTrue(ReplenishCalculator.forceAlert(5, 10));
        assertFalse(ReplenishCalculator.forceAlert(20, 10));
    }

    @Test
    public void turnoverAndCapital() {
        assertEquals(2.0, TurnoverCalculator.turnoverRate(20, 10), 0.001);
        assertEquals(15.0, TurnoverCalculator.turnoverDays(2.0, 30), 0.001);
        assertEquals(500.0, TurnoverCalculator.capitalOccupation(10, 50), 0.001);
    }

    @Test
    public void riskRules() {
        assertTrue(RiskCalculator.overstock(10, 0, 0));
        assertFalse(RiskCalculator.overstock(0, 0, 0));
        assertTrue(RiskCalculator.stockoutRisk(5, 10, 1.0, 14));
        assertTrue(RiskCalculator.stockoutRisk(10, 20, 2.0, 14)); // support days 5 < 14
        assertFalse(RiskCalculator.stockoutRisk(100, 10, 1.0, 14));
    }
}
