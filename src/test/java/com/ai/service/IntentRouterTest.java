package com.ai.service;

import com.ai.dto.AiIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntentRouterTest {

    private IntentRouter intentRouter;

    @BeforeEach
    public void setUp() {
        intentRouter = new IntentRouter();
    }

    @Test
    public void routeNullOrEmptyReturnsGeneral() {
        assertEquals(AiIntent.GENERAL, intentRouter.route(null));
        assertEquals(AiIntent.GENERAL, intentRouter.route(""));
        assertEquals(AiIntent.GENERAL, intentRouter.route("   "));
    }

    @Test
    public void routeFinanceKeywords() {
        assertEquals(AiIntent.FINANCE, intentRouter.route("本月应收应付汇总"));
        assertEquals(AiIntent.FINANCE, intentRouter.route("未付款订单有哪些"));
    }

    @Test
    public void routeReplenishKeywords() {
        assertEquals(AiIntent.REPLENISH, intentRouter.route("哪些SKU需要补货"));
        assertEquals(AiIntent.REPLENISH, intentRouter.route("采购建议"));
    }

    @Test
    public void routeSellThroughKeywords() {
        assertEquals(AiIntent.SELL_THROUGH, intentRouter.route("动销分析"));
        assertEquals(AiIntent.SELL_THROUGH, intentRouter.route("滞销商品Top"));
    }

    @Test
    public void routeTurnoverKeywords() {
        assertEquals(AiIntent.TURNOVER, intentRouter.route("库存周转率"));
        assertEquals(AiIntent.TURNOVER, intentRouter.route("资金占用估算"));
    }

    @Test
    public void routeRiskKeywords() {
        assertEquals(AiIntent.RISK, intentRouter.route("缺货风险预警"));
        assertEquals(AiIntent.RISK, intentRouter.route("库龄积压"));
    }

    @Test
    public void routeInventoryKeywords() {
        assertEquals(AiIntent.INVENTORY, intentRouter.route("库存分类汇总"));
        assertEquals(AiIntent.INVENTORY, intentRouter.route("入库出库台账"));
    }

    @Test
    public void routeUnmatchedReturnsGeneral() {
        assertEquals(AiIntent.GENERAL, intentRouter.route("你好，帮我看看整体情况"));
    }
}
