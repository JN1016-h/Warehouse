package com.ai.service;

import com.ai.dto.AiIntent;
import org.springframework.stereotype.Component;

/**
 * 关键词意图路由
 */
@Component
public class IntentRouter {

    public AiIntent route(String question) {
        if (question == null || question.trim().isEmpty()) {
            return AiIntent.GENERAL;
        }
        String q = question.trim();
        if (containsAny(q, "应收", "应付", "回款", "账款", "财务", "付款状态", "未付款")) {
            return AiIntent.FINANCE;
        }
        if (containsAny(q, "补货", "采购建议", "建议补货", "备货")) {
            return AiIntent.REPLENISH;
        }
        if (containsAny(q, "动销", "畅销", "滞销", "平销", "积压预警")) {
            return AiIntent.SELL_THROUGH;
        }
        if (containsAny(q, "周转", "资金占用", "占用估算")) {
            return AiIntent.TURNOVER;
        }
        if (containsAny(q, "积压", "库龄", "缺货", "风险", "预警", "低于阈值")) {
            return AiIntent.RISK;
        }
        if (containsAny(q, "库存", "出库", "入库", "台账", "分类汇总")) {
            return AiIntent.INVENTORY;
        }
        return AiIntent.GENERAL;
    }

    private boolean containsAny(String text, String... keys) {
        for (String key : keys) {
            if (text.contains(key)) {
                return true;
            }
        }
        return false;
    }
}
