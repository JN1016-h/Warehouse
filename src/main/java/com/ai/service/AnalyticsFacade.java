package com.ai.service;

import com.ai.algorithm.ReplenishCalculator;
import com.ai.algorithm.RiskCalculator;
import com.ai.algorithm.SellThroughCalculator;
import com.ai.algorithm.TurnoverCalculator;
import com.ai.config.AiProperties;
import com.ai.dto.AiIntent;
import com.ai.dto.TimeRange;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.dao.ShangpinxinxiDao;
import com.dto.FinanceSummary;
import com.dto.PayableQuery;
import com.dto.ReceivableQuery;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import com.entity.ShangpinxinxiEntity;
import com.service.FinanceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 本地只读统计装配（数字先算）
 */
@Service
public class AnalyticsFacade {

    private final ShangpinxinxiDao shangpinxinxiDao;
    private final ChukuxinxiDao chukuxinxiDao;
    private final RukuxinxiDao rukuxinxiDao;
    private final DinghuoxinxiDao dinghuoxinxiDao;
    private final FinanceService financeService;
    private final AiProperties aiProperties;
    private final TimeRangeResolver timeRangeResolver;

    public AnalyticsFacade(ShangpinxinxiDao shangpinxinxiDao,
                           ChukuxinxiDao chukuxinxiDao,
                           RukuxinxiDao rukuxinxiDao,
                           DinghuoxinxiDao dinghuoxinxiDao,
                           FinanceService financeService,
                           AiProperties aiProperties,
                           TimeRangeResolver timeRangeResolver) {
        this.shangpinxinxiDao = shangpinxinxiDao;
        this.chukuxinxiDao = chukuxinxiDao;
        this.rukuxinxiDao = rukuxinxiDao;
        this.dinghuoxinxiDao = dinghuoxinxiDao;
        this.financeService = financeService;
        this.aiProperties = aiProperties;
        this.timeRangeResolver = timeRangeResolver;
    }

    public Map<String, Object> assemble(AiIntent intent, TimeRange range, boolean financeOk) {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("timeRange", range.getLabel());
        root.put("start", range.getStart());
        root.put("end", range.getEnd());
        root.put("note", "以下数字均来自本地业务库聚合，禁止模型编造。");

        List<ShangpinxinxiEntity> products = shangpinxinxiDao.selectList(new EntityWrapper<ShangpinxinxiEntity>());
        if (products == null) {
            products = Collections.emptyList();
        }
        List<ChukuxinxiEntity> outbounds = queryOutbound(range.getStart(), range.getEnd());
        List<RukuxinxiEntity> inbounds = queryInbound(range.getStart(), range.getEnd());

        Map<String, Integer> outboundQty = aggregateOutboundQty(outbounds);
        Map<String, Date> lastOutbound = lastOutboundTime(outbounds);
        Map<String, Date> lastInbound = lastInboundTime(inbounds);
        Map<String, Double> priceMap = latestSalePrice();

        boolean empty = products.isEmpty() && outboundQty.isEmpty();
        root.put("hasData", !empty);

        switch (intent) {
            case SELL_THROUGH:
                root.put("sellThrough", buildSellThrough(products, outboundQty));
                break;
            case REPLENISH:
                root.put("replenish", buildReplenish(products, outboundQty));
                break;
            case TURNOVER:
                root.put("turnover", buildTurnover(products, outboundQty, priceMap, range, financeOk));
                break;
            case RISK:
                root.put("risk", buildRisk(products, outboundQty, lastOutbound, lastInbound, range));
                break;
            case FINANCE:
                if (financeOk) {
                    root.put("finance", buildFinance(range));
                } else {
                    root.put("permissionDenied", true);
                    root.put("permissionMessage", "当前角色无财务数据权限，无法查询应收应付/回款类信息。");
                }
                break;
            case INVENTORY:
                root.put("inventory", buildInventory(products, outboundQty, inbounds));
                break;
            case GENERAL:
            default:
                root.put("inventory", buildInventory(products, outboundQty, inbounds));
                root.put("sellThrough", buildSellThrough(products, outboundQty));
                root.put("risk", buildRisk(products, outboundQty, lastOutbound, lastInbound, range));
                break;
        }
        return root;
    }

    private List<ChukuxinxiEntity> queryOutbound(Date start, Date end) {
        EntityWrapper<ChukuxinxiEntity> ew = new EntityWrapper<ChukuxinxiEntity>();
        if (start != null) {
            ew.ge("jiaohuoshijian", start);
        }
        if (end != null) {
            ew.le("jiaohuoshijian", end);
        }
        List<ChukuxinxiEntity> list = chukuxinxiDao.selectList(ew);
        return list == null ? Collections.<ChukuxinxiEntity>emptyList() : list;
    }

    private List<RukuxinxiEntity> queryInbound(Date start, Date end) {
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
        if (start != null) {
            ew.ge("rukushijian", start);
        }
        if (end != null) {
            ew.le("rukushijian", end);
        }
        List<RukuxinxiEntity> list = rukuxinxiDao.selectList(ew);
        return list == null ? Collections.<RukuxinxiEntity>emptyList() : list;
    }

    private Map<String, Integer> aggregateOutboundQty(List<ChukuxinxiEntity> list) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (ChukuxinxiEntity row : list) {
            if (row.getFuzhuangbianhao() == null) {
                continue;
            }
            int qty = row.getFuzhuangkucun() == null ? 0 : row.getFuzhuangkucun();
            Integer old = map.get(row.getFuzhuangbianhao());
            map.put(row.getFuzhuangbianhao(), (old == null ? 0 : old) + qty);
        }
        return map;
    }

    private Map<String, Date> lastOutboundTime(List<ChukuxinxiEntity> list) {
        Map<String, Date> map = new HashMap<String, Date>();
        for (ChukuxinxiEntity row : list) {
            if (row.getFuzhuangbianhao() == null || row.getJiaohuoshijian() == null) {
                continue;
            }
            Date old = map.get(row.getFuzhuangbianhao());
            if (old == null || row.getJiaohuoshijian().after(old)) {
                map.put(row.getFuzhuangbianhao(), row.getJiaohuoshijian());
            }
        }
        return map;
    }

    private Map<String, Date> lastInboundTime(List<RukuxinxiEntity> list) {
        Map<String, Date> map = new HashMap<String, Date>();
        // also scan all inbound for age if window empty
        List<RukuxinxiEntity> all = rukuxinxiDao.selectList(new EntityWrapper<RukuxinxiEntity>());
        if (all == null) {
            all = list;
        }
        for (RukuxinxiEntity row : all) {
            if (row.getFuzhuangbianhao() == null || row.getRukushijian() == null) {
                continue;
            }
            Date old = map.get(row.getFuzhuangbianhao());
            if (old == null || row.getRukushijian().after(old)) {
                map.put(row.getFuzhuangbianhao(), row.getRukushijian());
            }
        }
        return map;
    }

    private Map<String, Double> latestSalePrice() {
        Map<String, Double> map = new HashMap<String, Double>();
        List<DinghuoxinxiEntity> orders = dinghuoxinxiDao.selectList(
                new EntityWrapper<DinghuoxinxiEntity>().orderBy("dinghuoshijian", false));
        if (orders == null) {
            return map;
        }
        for (DinghuoxinxiEntity row : orders) {
            if (row.getFuzhuangbianhao() == null || row.getXiaoshoudanjia() == null) {
                continue;
            }
            if (!map.containsKey(row.getFuzhuangbianhao())) {
                map.put(row.getFuzhuangbianhao(), row.getXiaoshoudanjia());
            }
        }
        return map;
    }

    private Map<String, Object> buildInventory(List<ShangpinxinxiEntity> products,
                                               Map<String, Integer> outboundQty,
                                               List<RukuxinxiEntity> inbounds) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        int totalSku = products.size();
        int totalStock = 0;
        Map<String, Integer> byCategory = new HashMap<String, Integer>();
        List<Map<String, Object>> lowStock = new ArrayList<Map<String, Object>>();
        int topN = aiProperties.getTopN();

        for (ShangpinxinxiEntity p : products) {
            int stock = p.getFuzhuangkucun() == null ? 0 : p.getFuzhuangkucun();
            totalStock += stock;
            String cat = p.getShangpinfenlei() == null ? "未分类" : p.getShangpinfenlei();
            Integer old = byCategory.get(cat);
            byCategory.put(cat, (old == null ? 0 : old) + stock);

            Integer threshold = p.getKucunyuzhi();
            if (threshold != null && stock <= threshold && lowStock.size() < topN) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("sku", p.getFuzhuangbianhao());
                row.put("name", p.getFuzhuangmingcheng());
                row.put("stock", stock);
                row.put("threshold", threshold);
                lowStock.add(row);
            }
        }

        int inboundQty = 0;
        for (RukuxinxiEntity r : inbounds) {
            inboundQty += r.getFuzhuangkucun() == null ? 0 : r.getFuzhuangkucun();
        }
        int outboundTotal = 0;
        for (Integer v : outboundQty.values()) {
            outboundTotal += v == null ? 0 : v;
        }

        result.put("totalSku", totalSku);
        result.put("totalStock", totalStock);
        result.put("outboundQtyInWindow", outboundTotal);
        result.put("inboundQtyInWindow", inboundQty);
        result.put("stockByCategory", byCategory);
        result.put("lowStockTop", lowStock);
        return result;
    }

    private Map<String, Object> buildSellThrough(List<ShangpinxinxiEntity> products,
                                                 Map<String, Integer> outboundQty) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        int totalSku = products.size();
        Set<String> withOutbound = new HashSet<String>();
        List<Map<String, Object>> hot = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> normal = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> slow = new ArrayList<Map<String, Object>>();
        int topN = aiProperties.getTopN();

        for (ShangpinxinxiEntity p : products) {
            String sku = p.getFuzhuangbianhao();
            int out = outboundQty.containsKey(sku) ? outboundQty.get(sku) : 0;
            if (out > 0) {
                withOutbound.add(sku);
            }
            double avgStock = p.getFuzhuangkucun() == null ? 0 : p.getFuzhuangkucun();
            // 简化：周期平均库存用当前库存近似（论文可说明）
            double coeff = SellThroughCalculator.coefficient(out, avgStock);
            String level = SellThroughCalculator.level(coeff);
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("sku", sku);
            row.put("name", p.getFuzhuangmingcheng());
            row.put("outboundQty", out);
            row.put("avgStock", avgStock);
            row.put("coefficient", round2(coeff));
            row.put("level", level);
            if ("畅销".equals(level) && hot.size() < topN) {
                hot.add(row);
            } else if ("平销".equals(level) && normal.size() < topN) {
                normal.add(row);
            } else if ("滞销".equals(level) && slow.size() < topN) {
                slow.add(row);
            }
        }

        Collections.sort(hot, coeffDesc());
        Collections.sort(slow, coeffAsc());

        result.put("sellThroughRate", round2(SellThroughCalculator.sellThroughRate(withOutbound.size(), totalSku)));
        result.put("totalSku", totalSku);
        result.put("skusWithOutbound", withOutbound.size());
        result.put("hotTop", trim(hot, topN));
        result.put("normalTop", trim(normal, topN));
        result.put("slowTop", trim(slow, topN));
        result.put("formula", "动销系数=周期出库÷平均库存；>1.2畅销，0.4~1.2平销，<0.4滞销");
        return result;
    }

    private Map<String, Object> buildReplenish(List<ShangpinxinxiEntity> products,
                                               Map<String, Integer> windowOutbound) {
        // 近30天日均：单独取30天窗口
        TimeRange last30 = timeRangeResolver.lastDays(30);
        Map<String, Integer> out30 = aggregateOutboundQty(queryOutbound(last30.getStart(), last30.getEnd()));
        int defaultLead = aiProperties.getReplenish().getDefaultLeadDays();
        int topN = aiProperties.getTopN();
        List<Map<String, Object>> suggestions = new ArrayList<Map<String, Object>>();

        for (ShangpinxinxiEntity p : products) {
            String sku = p.getFuzhuangbianhao();
            int stock = p.getFuzhuangkucun() == null ? 0 : p.getFuzhuangkucun();
            int out = out30.containsKey(sku) ? out30.get(sku) : 0;
            double daily = ReplenishCalculator.dailyConsumption(out);
            int lead = ReplenishCalculator.resolveLeadDays(p.getCaigouzhouqi(), defaultLead);
            boolean force = ReplenishCalculator.forceAlert(stock, p.getKucunyuzhi());
            int suggest = ReplenishCalculator.suggestQty(daily, lead, stock, p.getKucunyuzhi());
            if (suggest <= 0 && !force) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("sku", sku);
            row.put("name", p.getFuzhuangmingcheng());
            row.put("currentStock", stock);
            row.put("threshold", p.getKucunyuzhi());
            row.put("dailyConsumption", round2(daily));
            row.put("leadDays", lead);
            row.put("suggestQty", suggest);
            row.put("forceAlert", force);
            suggestions.add(row);
        }
        Collections.sort(suggestions, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Integer sa = (Integer) a.get("suggestQty");
                Integer sb = (Integer) b.get("suggestQty");
                return Integer.compare(sb == null ? 0 : sb, sa == null ? 0 : sa);
            }
        });
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("defaultLeadDays", defaultLead);
        result.put("formula", "建议补货=max(0, 近30天日均×采购周期−当前库存)；库存≤阈值强制提醒");
        result.put("suggestions", trim(suggestions, topN));
        return result;
    }

    private Map<String, Object> buildTurnover(List<ShangpinxinxiEntity> products,
                                              Map<String, Integer> outboundQty,
                                              Map<String, Double> priceMap,
                                              TimeRange range,
                                              boolean financeOk) {
        long windowDays = range.days();
        int totalOutbound = 0;
        int totalStock = 0;
        double capital = 0.0;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        int topN = aiProperties.getTopN();

        for (ShangpinxinxiEntity p : products) {
            String sku = p.getFuzhuangbianhao();
            int stock = p.getFuzhuangkucun() == null ? 0 : p.getFuzhuangkucun();
            int out = outboundQty.containsKey(sku) ? outboundQty.get(sku) : 0;
            totalOutbound += out;
            totalStock += stock;
            double rate = TurnoverCalculator.turnoverRate(out, stock);
            double days = TurnoverCalculator.turnoverDays(rate, windowDays);
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("sku", sku);
            row.put("name", p.getFuzhuangmingcheng());
            row.put("outboundQty", out);
            row.put("avgStock", stock);
            row.put("turnoverRate", round2(rate));
            row.put("turnoverDays", round2(days));
            if (financeOk) {
                double price = priceMap.containsKey(sku) ? priceMap.get(sku) : 0.0;
                double occ = TurnoverCalculator.capitalOccupation(stock, price);
                capital += occ;
                row.put("unitPrice", price);
                row.put("capitalOccupation", round2(occ));
            }
            rows.add(row);
        }
        Collections.sort(rows, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Double ra = (Double) a.get("turnoverRate");
                Double rb = (Double) b.get("turnoverRate");
                return Double.compare(ra == null ? 0 : ra, rb == null ? 0 : rb);
            }
        });

        double avgStock = products.isEmpty() ? 0 : (totalStock * 1.0 / products.size());
        double overallRate = TurnoverCalculator.turnoverRate(totalOutbound, avgStock);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("windowDays", windowDays);
        result.put("totalOutboundQty", totalOutbound);
        result.put("avgStock", round2(avgStock));
        result.put("turnoverRate", round2(overallRate));
        result.put("turnoverDays", round2(TurnoverCalculator.turnoverDays(overallRate, windowDays)));
        result.put("items", trim(rows, topN));
        if (financeOk) {
            result.put("capitalOccupationTotal", round2(capital));
            result.put("capitalNote", "资金占用估算=当前库存×销售单价近似，非真实采购成本");
        } else {
            result.put("capitalNote", "仓管角色仅展示周转指标，不含资金占用金额");
        }
        return result;
    }

    private Map<String, Object> buildRisk(List<ShangpinxinxiEntity> products,
                                          Map<String, Integer> outboundQty,
                                          Map<String, Date> lastOutbound,
                                          Map<String, Date> lastInbound,
                                          TimeRange range) {
        TimeRange last30 = timeRangeResolver.lastDays(30);
        Map<String, Integer> out30 = aggregateOutboundQty(queryOutbound(last30.getStart(), last30.getEnd()));
        int defaultLead = aiProperties.getReplenish().getDefaultLeadDays();
        long now = System.currentTimeMillis();
        long windowDays = range.days();
        int topN = aiProperties.getTopN();

        List<Map<String, Object>> overstock = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> stockout = new ArrayList<Map<String, Object>>();

        for (ShangpinxinxiEntity p : products) {
            String sku = p.getFuzhuangbianhao();
            int stock = p.getFuzhuangkucun() == null ? 0 : p.getFuzhuangkucun();
            int out = outboundQty.containsKey(sku) ? outboundQty.get(sku) : 0;
            int out30Qty = out30.containsKey(sku) ? out30.get(sku) : 0;
            double daily = ReplenishCalculator.dailyConsumption(out30Qty);
            int lead = ReplenishCalculator.resolveLeadDays(p.getCaigouzhouqi(), defaultLead);

            Date lo = lastOutbound.get(sku);
            Date li = lastInbound.get(sku);
            int age = RiskCalculator.ageDays(
                    lo == null ? null : lo.getTime(),
                    li == null ? null : li.getTime(),
                    now,
                    windowDays);

            if (RiskCalculator.overstock(stock, out, 0)) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("sku", sku);
                row.put("name", p.getFuzhuangmingcheng());
                row.put("stock", stock);
                row.put("outboundInWindow", out);
                row.put("ageDays", age);
                overstock.add(row);
            }
            if (RiskCalculator.stockoutRisk(stock, p.getKucunyuzhi(), daily, lead)) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                row.put("sku", sku);
                row.put("name", p.getFuzhuangmingcheng());
                row.put("stock", stock);
                row.put("threshold", p.getKucunyuzhi());
                row.put("dailyConsumption", round2(daily));
                row.put("supportDays", Double.isInfinite(RiskCalculator.supportDays(stock, daily))
                        ? "充足" : String.valueOf(round2(RiskCalculator.supportDays(stock, daily))));
                row.put("leadDays", lead);
                stockout.add(row);
            }
        }

        Collections.sort(overstock, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Integer aa = (Integer) a.get("ageDays");
                Integer bb = (Integer) b.get("ageDays");
                return Integer.compare(bb == null ? 0 : bb, aa == null ? 0 : aa);
            }
        });

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("overstockTop", trim(overstock, topN));
        result.put("stockoutTop", trim(stockout, topN));
        result.put("note", "风险口径为库龄积压+阈值/可支撑天数缺货，不做临期预警");
        return result;
    }

    private Map<String, Object> buildFinance(TimeRange range) {
        ReceivableQuery rq = new ReceivableQuery();
        rq.setStartDate(range.getStart());
        rq.setEndDate(range.getEnd());
        PayableQuery pq = new PayableQuery();
        pq.setStartDate(range.getStart());
        pq.setEndDate(range.getEnd());
        FinanceSummary recv = financeService.calculateReceivableSummary(rq);
        FinanceSummary pay = financeService.calculatePayableSummary(pq);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("receivable", summaryMap(recv));
        result.put("payable", summaryMap(pay));
        return result;
    }

    private Map<String, Object> summaryMap(FinanceSummary s) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (s == null) {
            return m;
        }
        m.put("totalAmount", s.getTotalAmount());
        m.put("paidAmount", s.getPaidAmount());
        m.put("unpaidAmount", s.getUnpaidAmount());
        m.put("totalCount", s.getTotalCount());
        m.put("paidCount", s.getPaidCount());
        m.put("unpaidCount", s.getUnpaidCount());
        return m;
    }

    private Comparator<Map<String, Object>> coeffDesc() {
        return new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Double ca = (Double) a.get("coefficient");
                Double cb = (Double) b.get("coefficient");
                return Double.compare(cb == null ? 0 : cb, ca == null ? 0 : ca);
            }
        };
    }

    private Comparator<Map<String, Object>> coeffAsc() {
        return new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                Double ca = (Double) a.get("coefficient");
                Double cb = (Double) b.get("coefficient");
                return Double.compare(ca == null ? 0 : ca, cb == null ? 0 : cb);
            }
        };
    }

    private List<Map<String, Object>> trim(List<Map<String, Object>> list, int n) {
        if (list == null || list.size() <= n) {
            return list;
        }
        return new ArrayList<Map<String, Object>>(list.subList(0, n));
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
