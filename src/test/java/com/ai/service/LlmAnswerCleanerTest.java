package com.ai.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LlmAnswerCleanerTest {

    @Test
    public void dropPureThinkingProcess() {
        String raw = "Thinking Process:\n\n1. **Analyze the Request:**\n"
                + "* Role: Professional Warehouse Data Analysis Assistant\n"
                + "* Constraint 1: Only answer based on Business Data\n"
                + "2. **Analyze the Business Data:**\n"
                + "* slowTop is empty\n"
                + "6. **Refining based on Constraints:**\n"
                + "Let's interpret the empty array...";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void keepChineseAnswer() {
        String raw = "## 滞销商品分析\n\n- 当前周期暂无滞销 SKU\n- 整体动销率 100%";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("滞销商品分析"));
    }

    @Test
    public void stripThinkTags() {
        String raw = "<think>internal</think>\n## 补货建议\n\n建议补货数量见下表";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertFalse(cleaned.contains("<think>"));
        assertTrue(cleaned.contains("补货建议"));
    }

    @Test
    public void nullAndEmptyInput() {
        assertNull(LlmAnswerCleaner.clean(null));
        assertNull(LlmAnswerCleaner.clean(""));
        assertNull(LlmAnswerCleaner.clean("   "));
    }

    @Test
    public void stripThinkTagBlocks() {
        String raw = "<think>internal</think>\n## 库存分析\n\n当前库存正常";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存分析"));
        assertFalse(cleaned.contains("internal"));
    }

    @Test
    public void extractAfterThinkingMarker() {
        String raw = "Thinking Process:\n\n1. Analyze the Request\n\n最终回答\n\n## 周转分析\n\n周转率良好";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("周转分析"));
    }

    @Test
    public void keepMarkdownTableWithoutChinese() {
        String raw = "|sku|qty|\n|---|---|\n|A|1|";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("|---|"));
    }

    @Test
    public void keepNoDataMessage() {
        String raw = "暂无相关库存统计数据";
        assertEquals(raw, LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void dropMostlyThinkingContent() {
        String raw = "Thinking Process:\nAnalyze the Request\nAnalyze the Business Data\nConstraint 1\nFormulate the Response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void stripThinkTagWithThinkingName() {
        String raw = "<thinking>internal reasoning</thinking>\n## 销售分析\n\n本季度销售表现良好";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("销售分析"));
        assertFalse(cleaned.contains("internal reasoning"));
    }

    @Test
    public void stripCodeBlockThinking() {
        String raw = "```thinking\nstep1\nstep2\n```\n## 库存报告\n\n库存水平正常";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存报告"));
    }

    @Test
    public void extractAfterFinalAnswerMarker() {
        String raw = "Thinking Process:\n\n1. Analyze\n\nFinal Answer\n\n## 结论摘要\n\n所有指标均在正常范围内";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("结论摘要"));
    }

    @Test
    public void extractAfterChineseConclusionMarker() {
        String raw = "Thinking Process:\n\n分析中\n\n结论：当前库存周转率符合预期目标";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存周转率"));
    }

    @Test
    public void keepContentBeforeEmbeddedThinking() {
        String raw = "## 直接回答\n\n这是有效中文回答内容足够长\n\nThinking Process:\nAnalyze the Request";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("直接回答"));
    }

    @Test
    public void dropEmbeddedThinkingWithoutUsefulAnswer() {
        String raw = "prefix\nThinking Process:\nAnalyze the Request\nAnalyze the Business Data\nConstraint 1";
        assertNull(LlmAnswerCleaner.clean(raw));
    }
}
