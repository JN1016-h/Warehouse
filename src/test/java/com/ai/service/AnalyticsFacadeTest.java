package com.ai.service;

import com.ai.config.AiProperties;
import com.ai.dto.AiIntent;
import com.ai.dto.TimeRange;
import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.dao.ShangpinxinxiDao;
import com.dto.FinanceSummary;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import com.entity.ShangpinxinxiEntity;
import com.service.FinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AnalyticsFacadeTest {

    @Mock
    private ShangpinxinxiDao shangpinxinxiDao;
    @Mock
    private ChukuxinxiDao chukuxinxiDao;
    @Mock
    private RukuxinxiDao rukuxinxiDao;
    @Mock
    private DinghuoxinxiDao dinghuoxinxiDao;
    @Mock
    private FinanceService financeService;
    @Mock
    private AiProperties aiProperties;
    @Mock
    private TimeRangeResolver timeRangeResolver;

    @InjectMocks
    private AnalyticsFacade analyticsFacade;

    private TimeRange range;
    private Date now;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        range = new TimeRange(cal.getTime(), now, "近月");

        AiProperties.Replenish replenish = new AiProperties.Replenish();
        replenish.setDefaultLeadDays(14);
        when(aiProperties.getTopN()).thenReturn(5);
        when(aiProperties.getReplenish()).thenReturn(replenish);
        when(timeRangeResolver.lastDays(30)).thenReturn(range);

        when(shangpinxinxiDao.selectList(any())).thenReturn(sampleProducts());
        when(chukuxinxiDao.selectList(any())).thenReturn(sampleOutbounds());
        when(rukuxinxiDao.selectList(any())).thenReturn(sampleInbounds());
        when(dinghuoxinxiDao.selectList(any())).thenReturn(sampleOrders());

        FinanceSummary recv = new FinanceSummary();
        recv.setTotalAmount(BigDecimal.valueOf(1000));
        recv.setPaidCount(1);
        recv.setUnpaidCount(1);
        recv.setTotalCount(2);
        when(financeService.calculateReceivableSummary(any())).thenReturn(recv);
        when(financeService.calculatePayableSummary(any())).thenReturn(recv);
    }

    @Test
    public void assembleSellThrough() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);

        assertEquals("近月", result.get("timeRange"));
        assertTrue((Boolean) result.get("hasData"));
        assertNotNull(result.get("sellThrough"));
        @SuppressWarnings("unchecked")
        Map<String, Object> st = (Map<String, Object>) result.get("sellThrough");
        assertNotNull(st.get("sellThroughRate"));
        assertNotNull(st.get("hotTop"));
        assertNull(result.get("finance"));
    }

    @Test
    public void assembleReplenish() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.REPLENISH, range, true);

        assertNotNull(result.get("replenish"));
        @SuppressWarnings("unchecked")
        Map<String, Object> rep = (Map<String, Object>) result.get("replenish");
        assertNotNull(rep.get("suggestions"));
    }

    @Test
    public void assembleTurnoverWithFinance() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, true);

        @SuppressWarnings("unchecked")
        Map<String, Object> turnover = (Map<String, Object>) result.get("turnover");
        assertNotNull(turnover.get("capitalOccupationTotal"));
        assertTrue(String.valueOf(turnover.get("capitalNote")).contains("资金占用"));
    }

    @Test
    public void assembleTurnoverWithoutFinance() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, false);

        @SuppressWarnings("unchecked")
        Map<String, Object> turnover = (Map<String, Object>) result.get("turnover");
        assertNull(turnover.get("capitalOccupationTotal"));
        assertTrue(String.valueOf(turnover.get("capitalNote")).contains("仓管角色"));
    }

    @Test
    public void assembleRisk() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);

        @SuppressWarnings("unchecked")
        Map<String, Object> risk = (Map<String, Object>) result.get("risk");
        assertNotNull(risk.get("overstockTop"));
        assertNotNull(risk.get("stockoutTop"));
    }

    @Test
    public void assembleFinanceOk() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.FINANCE, range, true);

        assertNotNull(result.get("finance"));
        assertNull(result.get("permissionDenied"));
    }

    @Test
    public void assembleFinanceDenied() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.FINANCE, range, false);

        assertTrue((Boolean) result.get("permissionDenied"));
        assertNull(result.get("finance"));
    }

    @Test
    public void assembleInventory() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);

        @SuppressWarnings("unchecked")
        Map<String, Object> inv = (Map<String, Object>) result.get("inventory");
        assertEquals(3, inv.get("totalSku"));
        assertNotNull(inv.get("lowStockTop"));
    }

    @Test
    public void assembleGeneralIncludesMultipleSections() {
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.GENERAL, range, true);

        assertNotNull(result.get("inventory"));
        assertNotNull(result.get("sellThrough"));
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleEmptyData() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.<ShangpinxinxiEntity>emptyList());
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);

        assertFalse((Boolean) result.get("hasData"));
    }

    @Test
    public void assembleNullProductList() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(null);
        when(chukuxinxiDao.selectList(any())).thenReturn(null);
        when(rukuxinxiDao.selectList(any())).thenReturn(null);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(null);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);

        assertFalse((Boolean) result.get("hasData"));
    }

    @Test
    public void assembleFinanceSummaryNull() {
        when(financeService.calculateReceivableSummary(any())).thenReturn(null);
        when(financeService.calculatePayableSummary(any())).thenReturn(null);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.FINANCE, range, true);

        @SuppressWarnings("unchecked")
        Map<String, Object> finance = (Map<String, Object>) result.get("finance");
        assertNotNull(finance.get("receivable"));
        assertNotNull(finance.get("payable"));
    }

    @Test
    public void assembleReplenishWithNullLeadDays() {
        List<ShangpinxinxiEntity> products = sampleProducts();
        products.get(2).setCaigouzhouqi(null);
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.REPLENISH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> rep = (Map<String, Object>) result.get("replenish");
        assertNotNull(rep.get("suggestions"));
    }

    @Test
    public void assembleRiskWithEmptyOverstock() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(product("P004", "新款", "上衣", 100, 5, 7, 50)));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> risk = (Map<String, Object>) result.get("risk");
        assertNotNull(risk.get("overstockTop"));
    }

    @Test
    public void assembleInventoryWithNullThreshold() {
        ShangpinxinxiEntity p = product("P005", "无阈值", "上衣", 2, 10, 7, 0);
        p.setKucunyuzhi(null);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> inv = (Map<String, Object>) result.get("inventory");
        assertNotNull(inv.get("lowStockTop"));
    }

    @Test
    public void assembleSellThroughWithNullSkuOutbound() {
        List<ChukuxinxiEntity> outbounds = new ArrayList<ChukuxinxiEntity>();
        ChukuxinxiEntity e = new ChukuxinxiEntity();
        e.setFuzhuangbianhao(null);
        e.setFuzhuangkucun(5);
        e.setJiaohuoshijian(daysAgo(1));
        outbounds.add(e);
        when(chukuxinxiDao.selectList(any())).thenReturn(outbounds);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        assertTrue((Boolean) result.get("hasData"));
    }

    @Test
    public void assembleSellThroughAllLevels() {
        List<ShangpinxinxiEntity> products = new ArrayList<ShangpinxinxiEntity>();
        products.add(product("HOT", "畅销", "上衣", 10, 5, 7, 0));
        products.add(product("NORM", "平销", "裤子", 10, 5, 7, 0));
        products.add(product("SLOW", "滞销", "外套", 10, 5, 7, 0));
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);

        List<ChukuxinxiEntity> outbounds = new ArrayList<ChukuxinxiEntity>();
        outbounds.add(outbound("HOT", 50, daysAgo(1)));
        outbounds.add(outbound("NORM", 5, daysAgo(2)));
        outbounds.add(outbound("SLOW", 1, daysAgo(3)));
        when(chukuxinxiDao.selectList(any())).thenReturn(outbounds);
        when(aiProperties.getTopN()).thenReturn(10);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> st = (Map<String, Object>) result.get("sellThrough");
        assertNotNull(st.get("hotTop"));
        assertNotNull(st.get("normalTop"));
        assertNotNull(st.get("slowTop"));
    }

    @Test
    public void assembleReplenishSkipsHealthyStock() {
        ShangpinxinxiEntity healthy = product("OK", "充足", "上衣", 500, 5, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(healthy));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.REPLENISH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> rep = (Map<String, Object>) result.get("replenish");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suggestions = (List<Map<String, Object>>) rep.get("suggestions");
        assertTrue(suggestions.isEmpty());
    }

    @Test
    public void assembleReplenishForceAlertDespiteZeroSuggest() {
        ShangpinxinxiEntity low = product("LOW", "低库存", "上衣", 1, 10, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(low));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.REPLENISH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> rep = (Map<String, Object>) result.get("replenish");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suggestions = (List<Map<String, Object>>) rep.get("suggestions");
        assertFalse(suggestions.isEmpty());
        assertTrue((Boolean) suggestions.get(0).get("forceAlert"));
    }

    @Test
    public void assembleReplenishTrimsToTopN() {
        when(aiProperties.getTopN()).thenReturn(1);
        List<ShangpinxinxiEntity> products = new ArrayList<ShangpinxinxiEntity>();
        products.add(product("A", "款A", "上衣", 1, 20, 7, 0));
        products.add(product("B", "款B", "裤子", 1, 20, 7, 0));
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.REPLENISH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> rep = (Map<String, Object>) result.get("replenish");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suggestions = (List<Map<String, Object>>) rep.get("suggestions");
        assertEquals(1, suggestions.size());
    }

    @Test
    public void assembleRiskInfiniteSupportDays() {
        ShangpinxinxiEntity p = product("SAFE", "充足", "上衣", 100, 5, 7, 0);
        p.setKucunyuzhi(50);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> risk = (Map<String, Object>) result.get("risk");
        assertNotNull(risk.get("overstockTop"));
    }

    @Test
    public void assembleInventoryNullCategoryAndNullInboundQty() {
        ShangpinxinxiEntity p = product("NC", "无分类", "上衣", 5, 0, 7, 0);
        p.setShangpinfenlei(null);
        p.setKucunyuzhi(null);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));

        RukuxinxiEntity inbound = inbound("NC", 0, daysAgo(1));
        inbound.setFuzhuangkucun(null);
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(inbound));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> inv = (Map<String, Object>) result.get("inventory");
        assertNotNull(inv.get("stockByCategory"));
    }

    @Test
    public void assembleWithNullTimeRangeBounds() {
        TimeRange openRange = new TimeRange(null, null, "全部");
        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, openRange, true);
        assertNotNull(result.get("inventory"));
    }

    @Test
    public void assembleDuplicateOutboundSkuAggregates() {
        List<ChukuxinxiEntity> outbounds = new ArrayList<ChukuxinxiEntity>();
        outbounds.add(outbound("P001", 10, daysAgo(5)));
        outbounds.add(outbound("P001", 15, daysAgo(3)));
        ChukuxinxiEntity older = outbound("P001", 5, daysAgo(20));
        outbounds.add(older);
        when(chukuxinxiDao.selectList(any())).thenReturn(outbounds);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        assertTrue((Boolean) result.get("hasData"));
    }

    @Test
    public void assembleOutboundNullDeliveryTimeSkipped() {
        ChukuxinxiEntity e = outbound("P001", 5, null);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(e));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleLatestSalePriceSkipsNullPrice() {
        List<DinghuoxinxiEntity> orders = new ArrayList<DinghuoxinxiEntity>();
        DinghuoxinxiEntity bad = new DinghuoxinxiEntity();
        bad.setFuzhuangbianhao("P001");
        bad.setXiaoshoudanjia(null);
        orders.add(bad);
        DinghuoxinxiEntity good = new DinghuoxinxiEntity();
        good.setFuzhuangbianhao("P001");
        good.setXiaoshoudanjia(88.0);
        good.setDinghuoshijian(daysAgo(2));
        orders.add(good);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(orders);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> turnover = (Map<String, Object>) result.get("turnover");
        assertNotNull(turnover.get("items"));
    }

    @Test
    public void assembleTurnoverEmptyProducts() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.<ShangpinxinxiEntity>emptyList());
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, false);
        @SuppressWarnings("unchecked")
        Map<String, Object> turnover = (Map<String, Object>) result.get("turnover");
        assertEquals(0.0, turnover.get("avgStock"));
    }

    @Test
    public void assembleInboundScanWhenAllNull() {
        when(rukuxinxiDao.selectList(any())).thenReturn(null);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleInboundNullSkuSkipped() {
        RukuxinxiEntity bad = inbound(null, 5, daysAgo(1));
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(bad));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleNullStockProducts() {
        ShangpinxinxiEntity p = product("NS", "无库存", "上衣", 0, 5, 7, 0);
        p.setFuzhuangkucun(null);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        assertNotNull(result.get("sellThrough"));
    }

    @Test
    public void assembleRiskWithStockoutRisk() {
        ShangpinxinxiEntity p = product("LOW", "缺货", "上衣", 2, 20, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));

        List<ChukuxinxiEntity> out30 = new ArrayList<ChukuxinxiEntity>();
        out30.add(outbound("LOW", 300, daysAgo(1)));
        when(chukuxinxiDao.selectList(any())).thenReturn(out30);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> risk = (Map<String, Object>) result.get("risk");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stockout = (List<Map<String, Object>>) risk.get("stockoutTop");
        assertFalse(stockout.isEmpty());
    }

    @Test
    public void assembleSellThroughTrimsHotAndSlowLists() {
        when(aiProperties.getTopN()).thenReturn(1);
        List<ShangpinxinxiEntity> products = new ArrayList<ShangpinxinxiEntity>();
        products.add(product("H1", "热1", "上衣", 5, 1, 7, 0));
        products.add(product("H2", "热2", "上衣", 5, 1, 7, 0));
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);

        List<ChukuxinxiEntity> outbounds = new ArrayList<ChukuxinxiEntity>();
        outbounds.add(outbound("H1", 50, daysAgo(1)));
        outbounds.add(outbound("H2", 40, daysAgo(2)));
        when(chukuxinxiDao.selectList(any())).thenReturn(outbounds);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> st = (Map<String, Object>) result.get("sellThrough");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> hot = (List<Map<String, Object>>) st.get("hotTop");
        assertEquals(1, hot.size());
    }

    @Test
    public void assembleInventoryLowStockAtTopNLimit() {
        when(aiProperties.getTopN()).thenReturn(1);
        List<ShangpinxinxiEntity> products = new ArrayList<ShangpinxinxiEntity>();
        products.add(product("L1", "低1", "上衣", 1, 10, 7, 0));
        products.add(product("L2", "低2", "裤子", 2, 10, 7, 0));
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> inv = (Map<String, Object>) result.get("inventory");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> low = (List<Map<String, Object>>) inv.get("lowStockTop");
        assertEquals(1, low.size());
    }

    @Test
    public void assembleOutboundNullQtyTreatedAsZero() {
        ChukuxinxiEntity e = outbound("P001", 0, daysAgo(1));
        e.setFuzhuangkucun(null);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(e));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        assertNotNull(result.get("sellThrough"));
    }

    @Test
    public void assembleHasDataTrueWhenOnlyOutbound() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.<ShangpinxinxiEntity>emptyList());
        when(chukuxinxiDao.selectList(any())).thenReturn(sampleOutbounds());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        assertTrue((Boolean) result.get("hasData"));
    }

    @Test
    public void assembleHasDataFalseWhenBothEmpty() {
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.<ShangpinxinxiEntity>emptyList());
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        assertFalse((Boolean) result.get("hasData"));
    }

    @Test
    public void coeffComparatorsHandleNullValues() throws Exception {
        Method desc = AnalyticsFacade.class.getDeclaredMethod("coeffDesc");
        desc.setAccessible(true);
        @SuppressWarnings("unchecked")
        Comparator<Map<String, Object>> descCmp = (Comparator<Map<String, Object>>) desc.invoke(analyticsFacade);

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("coefficient", null);
        Map<String, Object> b = new HashMap<String, Object>();
        b.put("coefficient", 1.5);
        assertTrue(descCmp.compare(a, b) > 0);
        assertTrue(descCmp.compare(b, a) < 0);

        Method asc = AnalyticsFacade.class.getDeclaredMethod("coeffAsc");
        asc.setAccessible(true);
        @SuppressWarnings("unchecked")
        Comparator<Map<String, Object>> ascCmp = (Comparator<Map<String, Object>>) asc.invoke(analyticsFacade);
        assertTrue(ascCmp.compare(a, b) < 0);
    }

    @Test
    public void assembleTurnoverSkuWithoutOutboundEntry() {
        ShangpinxinxiEntity p = product("NOOUT", "无出库", "上衣", 10, 5, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> turnover = (Map<String, Object>) result.get("turnover");
        assertNotNull(turnover.get("items"));
    }

    @Test
    public void trimHandlesNullAndOversizedList() throws Exception {
        Method trim = AnalyticsFacade.class.getDeclaredMethod("trim", List.class, int.class);
        trim.setAccessible(true);
        assertNull(trim.invoke(analyticsFacade, null, 5));

        List<Map<String, Object>> small = new ArrayList<Map<String, Object>>();
        small.add(new HashMap<String, Object>());
        assertSame(small, trim.invoke(analyticsFacade, small, 5));

        List<Map<String, Object>> big = new ArrayList<Map<String, Object>>();
        big.add(new HashMap<String, Object>());
        big.add(new HashMap<String, Object>());
        big.add(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> trimmed = (List<Map<String, Object>>) trim.invoke(analyticsFacade, big, 2);
        assertEquals(2, trimmed.size());
    }

    @Test
    public void assembleLatestSalePriceSkipsDuplicateSku() {
        List<DinghuoxinxiEntity> orders = new ArrayList<DinghuoxinxiEntity>();
        DinghuoxinxiEntity first = new DinghuoxinxiEntity();
        first.setFuzhuangbianhao("P001");
        first.setXiaoshoudanjia(100.0);
        first.setDinghuoshijian(daysAgo(1));
        orders.add(first);
        DinghuoxinxiEntity second = new DinghuoxinxiEntity();
        second.setFuzhuangbianhao("P001");
        second.setXiaoshoudanjia(50.0);
        second.setDinghuoshijian(daysAgo(5));
        orders.add(second);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(orders);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, true);
        assertNotNull(result.get("turnover"));
    }

    @Test
    public void assembleRiskWithNullLastTimesUsesWindowDays() {
        ShangpinxinxiEntity p = product("NEW", "新品", "上衣", 50, 5, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.<RukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleInboundOlderDateNotReplaced() {
        List<RukuxinxiEntity> inbounds = new ArrayList<RukuxinxiEntity>();
        inbounds.add(inbound("P001", 5, daysAgo(20)));
        inbounds.add(inbound("P001", 3, daysAgo(5)));
        when(rukuxinxiDao.selectList(any())).thenReturn(inbounds);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleSellThroughNoOutboundForSku() {
        ShangpinxinxiEntity p = product("NONE", "无出库", "上衣", 10, 5, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        assertNotNull(result.get("sellThrough"));
    }

    @Test
    public void innerComparatorsHandleNullSortKeys() throws Exception {
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("suggestQty", null);
        Map<String, Object> b = new HashMap<String, Object>();
        b.put("suggestQty", 10);

        Class<?> repClass = Class.forName("com.ai.service.AnalyticsFacade$1");
        java.lang.reflect.Constructor<?> repCtor = repClass.getDeclaredConstructor(AnalyticsFacade.class);
        repCtor.setAccessible(true);
        Object repCmp = repCtor.newInstance(analyticsFacade);
        Method repCompare = repClass.getDeclaredMethod("compare", Map.class, Map.class);
        repCompare.setAccessible(true);
        assertTrue((Integer) repCompare.invoke(repCmp, a, b) > 0);

        Map<String, Object> ta = new HashMap<String, Object>();
        ta.put("turnoverRate", null);
        Map<String, Object> tb = new HashMap<String, Object>();
        tb.put("turnoverRate", 1.5);
        Class<?> turnClass = Class.forName("com.ai.service.AnalyticsFacade$2");
        java.lang.reflect.Constructor<?> turnCtor = turnClass.getDeclaredConstructor(AnalyticsFacade.class);
        turnCtor.setAccessible(true);
        Object turnCmp = turnCtor.newInstance(analyticsFacade);
        Method turnCompare = turnClass.getDeclaredMethod("compare", Map.class, Map.class);
        turnCompare.setAccessible(true);
        assertTrue((Integer) turnCompare.invoke(turnCmp, ta, tb) < 0);

        Map<String, Object> ra = new HashMap<String, Object>();
        ra.put("ageDays", null);
        Map<String, Object> rb = new HashMap<String, Object>();
        rb.put("ageDays", 5);
        Class<?> riskClass = Class.forName("com.ai.service.AnalyticsFacade$3");
        java.lang.reflect.Constructor<?> riskCtor = riskClass.getDeclaredConstructor(AnalyticsFacade.class);
        riskCtor.setAccessible(true);
        Object riskCmp = riskCtor.newInstance(analyticsFacade);
        Method riskCompare = riskClass.getDeclaredMethod("compare", Map.class, Map.class);
        riskCompare.setAccessible(true);
        assertTrue((Integer) riskCompare.invoke(riskCmp, ra, rb) > 0);
    }

    @Test
    public void assembleSellThroughNormalLevelProduct() {
        ShangpinxinxiEntity p = product("NORM", "平销款", "裤子", 10, 5, 7, 0);
        when(shangpinxinxiDao.selectList(any())).thenReturn(Collections.singletonList(p));
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(outbound("NORM", 8, daysAgo(1))));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.SELL_THROUGH, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> st = (Map<String, Object>) result.get("sellThrough");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> normal = (List<Map<String, Object>>) st.get("normalTop");
        assertFalse(normal.isEmpty());
    }

    @Test
    public void assembleInboundNullRukushijianSkipped() {
        RukuxinxiEntity bad = inbound("P001", 5, null);
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(bad));

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.RISK, range, true);
        assertNotNull(result.get("risk"));
    }

    @Test
    public void assembleLatestSalePriceSkipsNullSkuInOrders() {
        List<DinghuoxinxiEntity> orders = new ArrayList<DinghuoxinxiEntity>();
        DinghuoxinxiEntity bad = new DinghuoxinxiEntity();
        bad.setFuzhuangbianhao(null);
        bad.setXiaoshoudanjia(99.0);
        orders.add(bad);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(orders);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.TURNOVER, range, true);
        assertNotNull(result.get("turnover"));
    }

    @Test
    public void assembleInventoryAggregatesExistingCategory() {
        List<ShangpinxinxiEntity> products = new ArrayList<ShangpinxinxiEntity>();
        products.add(product("A", "款A", "上衣", 5, 0, 7, 0));
        products.add(product("B", "款B", "上衣", 3, 0, 7, 0));
        when(shangpinxinxiDao.selectList(any())).thenReturn(products);

        Map<String, Object> result = analyticsFacade.assemble(AiIntent.INVENTORY, range, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> inv = (Map<String, Object>) result.get("inventory");
        @SuppressWarnings("unchecked")
        Map<String, Integer> byCat = (Map<String, Integer>) inv.get("stockByCategory");
        assertEquals(8, byCat.get("上衣").intValue());
    }

    private List<ShangpinxinxiEntity> sampleProducts() {
        List<ShangpinxinxiEntity> list = new ArrayList<ShangpinxinxiEntity>();
        list.add(product("P001", "畅销款", "上衣", 10, 5, 7, 20));
        list.add(product("P002", "滞销款", "裤子", 200, 2, 50, 0));
        list.add(product("P003", "缺货款", "外套", 3, 15, 10, 30));
        return list;
    }

    private ShangpinxinxiEntity product(String sku, String name, String cat,
                                        int stock, int threshold, int lead, int outQtyHint) {
        ShangpinxinxiEntity p = new ShangpinxinxiEntity();
        p.setFuzhuangbianhao(sku);
        p.setFuzhuangmingcheng(name);
        p.setShangpinfenlei(cat);
        p.setFuzhuangkucun(stock);
        p.setKucunyuzhi(threshold);
        p.setCaigouzhouqi(lead);
        return p;
    }

    private List<ChukuxinxiEntity> sampleOutbounds() {
        List<ChukuxinxiEntity> list = new ArrayList<ChukuxinxiEntity>();
        list.add(outbound("P001", 50, daysAgo(5)));
        list.add(outbound("P002", 1, daysAgo(20)));
        list.add(outbound("P003", 25, daysAgo(2)));
        list.add(outbound(null, 10, daysAgo(1)));
        return list;
    }

    private ChukuxinxiEntity outbound(String sku, int qty, Date date) {
        ChukuxinxiEntity e = new ChukuxinxiEntity();
        e.setFuzhuangbianhao(sku);
        e.setFuzhuangkucun(qty);
        e.setJiaohuoshijian(date);
        return e;
    }

    private List<RukuxinxiEntity> sampleInbounds() {
        List<RukuxinxiEntity> list = new ArrayList<RukuxinxiEntity>();
        list.add(inbound("P001", 20, daysAgo(10)));
        list.add(inbound("P003", 5, daysAgo(3)));
        return list;
    }

    private RukuxinxiEntity inbound(String sku, int qty, Date date) {
        RukuxinxiEntity e = new RukuxinxiEntity();
        e.setFuzhuangbianhao(sku);
        e.setFuzhuangkucun(qty);
        e.setRukushijian(date);
        return e;
    }

    private List<DinghuoxinxiEntity> sampleOrders() {
        List<DinghuoxinxiEntity> list = new ArrayList<DinghuoxinxiEntity>();
        DinghuoxinxiEntity o1 = new DinghuoxinxiEntity();
        o1.setFuzhuangbianhao("P001");
        o1.setXiaoshoudanjia(99.0);
        o1.setDinghuoshijian(daysAgo(1));
        list.add(o1);
        DinghuoxinxiEntity o2 = new DinghuoxinxiEntity();
        o2.setFuzhuangbianhao("P002");
        o2.setXiaoshoudanjia(50.0);
        o2.setDinghuoshijian(daysAgo(2));
        list.add(o2);
        return list;
    }

    private Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }
}
