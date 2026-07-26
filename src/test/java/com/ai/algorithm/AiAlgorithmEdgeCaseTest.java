package com.ai.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 单元测试补充：边界值 / 异常输入 / 业务阈值。
 * CI: mvn test → JaCoCo + unit-summary.html
 */
@DisplayName("AI 算法边界与阈值单元测试")
public class AiAlgorithmEdgeCaseTest {

    @Test
    @DisplayName("动销率：零库存分母与分级边界")
    public void sellThroughEdge() {
        assertEquals(0.0, SellThroughCalculator.sellThroughRate(0, 0), 0.001);
        assertEquals(0.0, SellThroughCalculator.sellThroughRate(5, 0), 0.001);
        assertEquals(100.0, SellThroughCalculator.sellThroughRate(10, 10), 0.001);
        assertEquals("滞销", SellThroughCalculator.level(0.0));
        assertEquals("畅销", SellThroughCalculator.level(999.0));
        assertEquals(0.0, SellThroughCalculator.coefficient(0, 10), 0.001);
    }

    @Test
    @DisplayName("补货：负库存与零日销量不抛异常")
    public void replenishEdge() {
        assertEquals(14, ReplenishCalculator.resolveLeadDays(-1, 14));
        assertEquals(0, ReplenishCalculator.suggestQty(0.0, 14, 100, 10));
        assertTrue(ReplenishCalculator.suggestQty(5.0, 7, 0, 0) >= 0);
        assertFalse(ReplenishCalculator.forceAlert(50, 10));
        assertTrue(ReplenishCalculator.forceAlert(0, 1));
    }

    @Test
    @DisplayName("周转：零均库存与资金占用")
    public void turnoverEdge() {
        assertEquals(0.0, TurnoverCalculator.turnoverRate(0, 0), 0.001);
        // rate<=0 时返回窗口天数
        assertEquals(30.0, TurnoverCalculator.turnoverDays(0.0, 30), 0.001);
        assertEquals(0.0, TurnoverCalculator.capitalOccupation(0, 50), 0.001);
        assertEquals(0.0, TurnoverCalculator.capitalOccupation(10, 0), 0.001);
    }

    @Test
    @DisplayName("风险：缺货/积压组合条件")
    public void riskEdge() {
        assertFalse(RiskCalculator.overstock(0, 1, 1));
        assertTrue(RiskCalculator.overstock(1, 0, 0));
        // 库存低于阈值 → 缺货风险
        assertTrue(RiskCalculator.stockoutRisk(5, 10, 0.0, 14));
        assertTrue(RiskCalculator.stockoutRisk(1, 100, 10.0, 30));
        assertFalse(RiskCalculator.stockoutRisk(100, null, 1.0, 14));
    }
}
