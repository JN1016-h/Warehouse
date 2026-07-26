package com.ai.service;

import com.ai.dto.AiStyle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 本地统计模板降级（NFR3）：不调用大模型
 */
@Component
public class FallbackResponder {

    public String respond(String question, AiStyle style, Map<String, Object> data) {
        return respond(question, style, data, null);
    }

    public String respond(String question, AiStyle style, Map<String, Object> data, String reason) {
        StringBuilder sb = new StringBuilder();
        if (reason != null && !reason.trim().isEmpty()) {
            sb.append("【本地统计模式】").append(reason.trim()).append("\n\n");
        } else {
            sb.append("【本地统计模式】以下结论基于业务库聚合结果。\n\n");
        }
        if (data == null || Boolean.FALSE.equals(data.get("hasData"))) {
            sb.append("暂无相关库存统计数据\n");
            return sb.toString();
        }
        if (Boolean.TRUE.equals(data.get("permissionDenied"))) {
            sb.append(String.valueOf(data.get("permissionMessage"))).append("\n");
            return sb.toString();
        }

        sb.append("时间范围：").append(data.get("timeRange")).append("\n\n");

        if (data.containsKey("sellThrough")) {
            appendSellThrough(sb, castMap(data.get("sellThrough")), style);
        }
        if (data.containsKey("replenish")) {
            appendReplenish(sb, castMap(data.get("replenish")), style);
        }
        if (data.containsKey("turnover")) {
            appendTurnover(sb, castMap(data.get("turnover")), style);
        }
        if (data.containsKey("risk")) {
            appendRisk(sb, castMap(data.get("risk")), style);
        }
        if (data.containsKey("inventory")) {
            appendInventory(sb, castMap(data.get("inventory")), style);
        }
        if (data.containsKey("finance")) {
            appendFinance(sb, castMap(data.get("finance")), style);
        }

        if (style == AiStyle.DETAILED) {
            sb.append("\n建议动作：优先处理缺货强制提醒与滞销积压 SKU，再复核补货建议量与采购周期配置。\n");
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null;
    }

    private void appendSellThrough(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 动销分析\n");
        sb.append("- 整体动销率：").append(m.get("sellThroughRate")).append("%\n");
        sb.append("- 有出库 SKU / 总 SKU：").append(m.get("skusWithOutbound")).append(" / ").append(m.get("totalSku")).append("\n");
        appendTable(sb, "滞销 Top", listOf(m.get("slowTop")),
                new String[]{"sku", "name", "coefficient", "outboundQty", "level"});
        if (style == AiStyle.DETAILED) {
            sb.append("口径：").append(m.get("formula")).append("\n");
        }
        sb.append("\n");
    }

    private void appendReplenish(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 补货建议\n");
        appendTable(sb, "建议列表", listOf(m.get("suggestions")),
                new String[]{"sku", "name", "currentStock", "leadDays", "suggestQty", "forceAlert"});
        if (style == AiStyle.DETAILED) {
            sb.append("口径：").append(m.get("formula")).append("\n");
        }
        sb.append("\n");
    }

    private void appendTurnover(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 周转概况\n");
        sb.append("- 周转率：").append(m.get("turnoverRate")).append("\n");
        sb.append("- 周转天数：").append(m.get("turnoverDays")).append("\n");
        if (m.get("capitalOccupationTotal") != null) {
            sb.append("- 资金占用估算合计：").append(m.get("capitalOccupationTotal")).append("\n");
        }
        if (style == AiStyle.DETAILED && m.get("capitalNote") != null) {
            sb.append("- 说明：").append(m.get("capitalNote")).append("\n");
        }
        sb.append("\n");
    }

    private void appendRisk(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 库存风险\n");
        appendTable(sb, "长期积压", listOf(m.get("overstockTop")),
                new String[]{"sku", "name", "stock", "outboundInWindow", "ageDays"});
        appendTable(sb, "缺货风险", listOf(m.get("stockoutTop")),
                new String[]{"sku", "name", "stock", "threshold", "supportDays", "leadDays"});
        if (style == AiStyle.DETAILED && m.get("note") != null) {
            sb.append(m.get("note")).append("\n");
        }
        sb.append("\n");
    }

    private void appendInventory(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 库存摘要\n");
        sb.append("- SKU 数：").append(m.get("totalSku")).append("\n");
        sb.append("- 当前库存合计：").append(m.get("totalStock")).append("\n");
        sb.append("- 窗口出库量：").append(m.get("outboundQtyInWindow")).append("\n");
        sb.append("- 窗口入库量：").append(m.get("inboundQtyInWindow")).append("\n");
        if (style == AiStyle.DETAILED) {
            appendTable(sb, "低库存", listOf(m.get("lowStockTop")),
                    new String[]{"sku", "name", "stock", "threshold"});
        }
        sb.append("\n");
    }

    private void appendFinance(StringBuilder sb, Map<String, Object> m, AiStyle style) {
        if (m == null) {
            return;
        }
        sb.append("## 财务概况\n");
        Map<String, Object> recv = castMap(m.get("receivable"));
        Map<String, Object> pay = castMap(m.get("payable"));
        sb.append("|类型|总金额|已付|未付|总笔数|已付笔数|未付笔数|\n");
        sb.append("|---|---|---|---|---|---|---|\n");
        sb.append("|应收|")
                .append(val(recv, "totalAmount")).append("|")
                .append(val(recv, "paidAmount")).append("|")
                .append(val(recv, "unpaidAmount")).append("|")
                .append(val(recv, "totalCount")).append("|")
                .append(val(recv, "paidCount")).append("|")
                .append(val(recv, "unpaidCount")).append("|\n");
        sb.append("|应付|")
                .append(val(pay, "totalAmount")).append("|")
                .append(val(pay, "paidAmount")).append("|")
                .append(val(pay, "unpaidAmount")).append("|")
                .append(val(pay, "totalCount")).append("|")
                .append(val(pay, "paidCount")).append("|")
                .append(val(pay, "unpaidCount")).append("|\n\n");
        if (style == AiStyle.DETAILED) {
            sb.append("说明：金额来自订货总金额关联估算；状态取出入库 payment_status。\n");
        }
    }

    private String val(Map<String, Object> m, String key) {
        if (m == null || m.get(key) == null) {
            return "0";
        }
        return String.valueOf(m.get(key));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> listOf(Object obj) {
        if (obj instanceof List) {
            return (List<Map<String, Object>>) obj;
        }
        return null;
    }

    private void appendTable(StringBuilder sb, String title, List<Map<String, Object>> rows, String[] cols) {
        sb.append("### ").append(title).append("\n");
        if (rows == null || rows.isEmpty()) {
            sb.append("暂无相关库存统计数据\n");
            return;
        }
        sb.append("|");
        for (String c : cols) {
            sb.append(c).append("|");
        }
        sb.append("\n|");
        for (int i = 0; i < cols.length; i++) {
            sb.append("---|");
        }
        sb.append("\n");
        for (Map<String, Object> row : rows) {
            sb.append("|");
            for (String c : cols) {
                Object v = row.get(c);
                sb.append(v == null ? "" : v).append("|");
            }
            sb.append("\n");
        }
    }
}
