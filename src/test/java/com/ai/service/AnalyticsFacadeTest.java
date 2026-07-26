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
import java.util.Date;
import java.util.List;
import java.util.Map;

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
