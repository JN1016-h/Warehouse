package com.ai.service;

import com.ai.dto.AiStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FallbackResponderTest {

    private FallbackResponder fallbackResponder;

    @BeforeEach
    public void setUp() {
        fallbackResponder = new FallbackResponder();
    }

    @Test
    public void respondNoData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("hasData", false);

        String result = fallbackResponder.respond("动销如何", AiStyle.SIMPLE, data);

        assertTrue(result.contains("暂无相关库存统计数据"));
    }

    @Test
    public void respondNullData() {
        String result = fallbackResponder.respond("问题", AiStyle.SIMPLE, null);
        assertTrue(result.contains("暂无相关库存统计数据"));
    }

    @Test
    public void respondPermissionDenied() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("permissionDenied", true);
        data.put("permissionMessage", "无财务权限");

        String result = fallbackResponder.respond("应收", AiStyle.SIMPLE, data);

        assertTrue(result.contains("无财务权限"));
    }

    @Test
    public void respondWithReason() {
        Map<String, Object> data = buildFullData();
        String result = fallbackResponder.respond("分析", AiStyle.SIMPLE, data, "API Key 未配置");

        assertTrue(result.contains("API Key 未配置"));
        assertTrue(result.contains("动销分析"));
    }

    @Test
    public void respondSimpleStyle() {
        String result = fallbackResponder.respond("库存概况", AiStyle.SIMPLE, buildFullData());

        assertTrue(result.contains("时间范围：近月"));
        assertTrue(result.contains("## 动销分析"));
        assertTrue(result.contains("## 补货建议"));
        assertTrue(result.contains("## 周转概况"));
        assertTrue(result.contains("## 库存风险"));
        assertTrue(result.contains("## 库存摘要"));
        assertTrue(result.contains("## 财务概况"));
        assertFalse(result.contains("建议动作"));
    }

    @Test
    public void respondDetailedStyle() {
        String result = fallbackResponder.respond("全面分析", AiStyle.DETAILED, buildFullData());

        assertTrue(result.contains("口径："));
        assertTrue(result.contains("建议动作"));
        assertTrue(result.contains("低库存"));
    }

    @Test
    public void respondEmptyTableShowsNoData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("timeRange", "近月");
        Map<String, Object> sellThrough = new LinkedHashMap<String, Object>();
        sellThrough.put("sellThroughRate", 0);
        sellThrough.put("skusWithOutbound", 0);
        sellThrough.put("totalSku", 0);
        sellThrough.put("slowTop", new ArrayList<Map<String, Object>>());
        data.put("sellThrough", sellThrough);

        String result = fallbackResponder.respond("动销", AiStyle.SIMPLE, data);

        assertTrue(result.contains("滞销 Top"));
        assertTrue(result.contains("暂无相关库存统计数据"));
    }

    @Test
    public void respondHasDataFalseExplicit() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", false);
        String result = fallbackResponder.respond("库存", AiStyle.SIMPLE, data);
        assertTrue(result.contains("暂无相关库存统计数据"));
    }

    @Test
    public void respondWithNullSectionMaps() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("timeRange", "近月");
        data.put("sellThrough", null);
        data.put("replenish", "not-a-map");
        data.put("finance", null);

        String result = fallbackResponder.respond("分析", AiStyle.DETAILED, data);
        assertTrue(result.contains("时间范围：近月"));
        assertTrue(result.contains("建议动作"));
    }

    @Test
    public void respondFinanceWithNullSubMaps() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("timeRange", "近月");
        Map<String, Object> finance = new LinkedHashMap<String, Object>();
        finance.put("receivable", null);
        finance.put("payable", null);
        data.put("finance", finance);

        String result = fallbackResponder.respond("财务", AiStyle.DETAILED, data);
        assertTrue(result.contains("## 财务概况"));
        assertTrue(result.contains("|应收|0|"));
    }

    @Test
    public void respondPermissionDeniedWithoutMessage() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("permissionDenied", true);

        String result = fallbackResponder.respond("财务", AiStyle.SIMPLE, data);
        assertTrue(result.contains("null"));
    }

    @Test
    public void respondBlankReasonUsesDefaultHeader() {
        String result = fallbackResponder.respond("库存", AiStyle.SIMPLE, buildFullData(), "   ");
        assertTrue(result.contains("以下结论基于业务库聚合结果"));
    }

    private Map<String, Object> buildFullData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("timeRange", "近月");

        Map<String, Object> sellThrough = new LinkedHashMap<String, Object>();
        sellThrough.put("sellThroughRate", 66.67);
        sellThrough.put("skusWithOutbound", 2);
        sellThrough.put("totalSku", 3);
        sellThrough.put("formula", "动销公式");
        List<Map<String, Object>> slowTop = new ArrayList<Map<String, Object>>();
        Map<String, Object> slow = new LinkedHashMap<String, Object>();
        slow.put("sku", "SKU001");
        slow.put("name", "测试款");
        slow.put("coefficient", 0.1);
        slow.put("outboundQty", 1);
        slow.put("level", "滞销");
        slowTop.add(slow);
        sellThrough.put("slowTop", slowTop);
        data.put("sellThrough", sellThrough);

        Map<String, Object> replenish = new LinkedHashMap<String, Object>();
        replenish.put("formula", "补货公式");
        List<Map<String, Object>> suggestions = new ArrayList<Map<String, Object>>();
        Map<String, Object> sug = new LinkedHashMap<String, Object>();
        sug.put("sku", "SKU002");
        sug.put("name", "缺货款");
        sug.put("currentStock", 2);
        sug.put("leadDays", 14);
        sug.put("suggestQty", 50);
        sug.put("forceAlert", true);
        suggestions.add(sug);
        replenish.put("suggestions", suggestions);
        data.put("replenish", replenish);

        Map<String, Object> turnover = new LinkedHashMap<String, Object>();
        turnover.put("turnoverRate", 1.5);
        turnover.put("turnoverDays", 20.0);
        turnover.put("capitalOccupationTotal", 10000.0);
        turnover.put("capitalNote", "资金说明");
        data.put("turnover", turnover);

        Map<String, Object> risk = new LinkedHashMap<String, Object>();
        risk.put("note", "风险说明");
        List<Map<String, Object>> overstock = new ArrayList<Map<String, Object>>();
        Map<String, Object> os = new LinkedHashMap<String, Object>();
        os.put("sku", "SKU003");
        os.put("name", "积压款");
        os.put("stock", 500);
        os.put("outboundInWindow", 0);
        os.put("ageDays", 90);
        overstock.add(os);
        risk.put("overstockTop", overstock);
        List<Map<String, Object>> stockout = new ArrayList<Map<String, Object>>();
        Map<String, Object> so = new LinkedHashMap<String, Object>();
        so.put("sku", "SKU002");
        so.put("name", "缺货款");
        so.put("stock", 2);
        so.put("threshold", 10);
        so.put("supportDays", "0.5");
        so.put("leadDays", 14);
        stockout.add(so);
        risk.put("stockoutTop", stockout);
        data.put("risk", risk);

        Map<String, Object> inventory = new LinkedHashMap<String, Object>();
        inventory.put("totalSku", 3);
        inventory.put("totalStock", 600);
        inventory.put("outboundQtyInWindow", 30);
        inventory.put("inboundQtyInWindow", 10);
        List<Map<String, Object>> lowStock = new ArrayList<Map<String, Object>>();
        Map<String, Object> low = new LinkedHashMap<String, Object>();
        low.put("sku", "SKU002");
        low.put("name", "缺货款");
        low.put("stock", 2);
        low.put("threshold", 10);
        lowStock.add(low);
        inventory.put("lowStockTop", lowStock);
        data.put("inventory", inventory);

        Map<String, Object> finance = new LinkedHashMap<String, Object>();
        finance.put("receivable", summary(BigDecimal.valueOf(1000), 2, 1));
        finance.put("payable", summary(BigDecimal.valueOf(500), 1, 1));
        data.put("finance", finance);

        return data;
    }

    private Map<String, Object> summary(BigDecimal total, int paidCount, int unpaidCount) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("totalAmount", total);
        m.put("paidAmount", total.divide(BigDecimal.valueOf(2)));
        m.put("unpaidAmount", total.divide(BigDecimal.valueOf(2)));
        m.put("totalCount", paidCount + unpaidCount);
        m.put("paidCount", paidCount);
        m.put("unpaidCount", unpaidCount);
        return m;
    }
}
