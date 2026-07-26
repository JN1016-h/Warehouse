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

    @Test
    public void dropAfterThinkingWhenNoUsefulExtract() {
        String raw = "Thinking Process:\n\nAnalyze the Request\nAnalyze the Business Data";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void keepThinkingColonPrefixWithChineseAnswer() {
        String raw = "Thinking:\n\n## 库存摘要\n\n当前共有100个SKU库存充足";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存摘要"));
    }

    @Test
    public void extractViaMarkdownHeadingInThinkingBlock() {
        String raw = "Thinking Process:\n\n1. step\n\n### 周转结论\n\n周转天数正常范围内";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("周转结论"));
    }

    @Test
    public void extractViaListItemInThinkingBlock() {
        String raw = "Thinking Process:\n\n- 库存风险较低无需补货\n\nAnalyze the Request";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存风险"));
    }

    @Test
    public void dropMostlyThinkingSingleHit() {
        String raw = "Some prefix thinking process noise without enough Chinese content";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void keepExactlySixChineseChars() {
        String raw = "一二三四五六";
        assertEquals(raw, LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void dropFiveChineseCharsOnly() {
        assertNull(LlmAnswerCleaner.clean("一二三四五"));
    }

    @Test
    public void embeddedThinkingUsesAfterWhenBeforeEmpty() {
        String raw = "Thinking Process:\n\nAnalyze the Request\n\n回答：这是最终的中文回答内容足够长";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("最终的中文回答"));
    }

    @Test
    public void embeddedThinkingDropWhenNeitherSideUseful() {
        String raw = "short\nThinking Process:\nAnalyze the Request\nAnalyze the Business Data\nConstraint 1\nFormulate the Response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void stripRedactedThinkingInline() {
        String raw = "redacted_thinking secret /redacted_thinking\n## 分析结果\n\n动销率表现良好";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("分析结果"));
        assertFalse(cleaned.contains("secret"));
    }

    @Test
    public void extractAfterThinkingUsesLowerCaseMarker() {
        String raw = "Thinking Process:\n\nAnalyze\n\nfinal answer\n\n## 中文标题足够长\n\n正文内容";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("中文标题"));
    }

    @Test
    public void isMostlyThinkingSingleKeywordStillFailsWithoutChinese() {
        assertNull(LlmAnswerCleaner.clean("thinking process only english"));
    }

    @Test
    public void returnsNullWhenTrimmedEmptyAfterStrip() {
        String raw = "<thinking>x</thinking>\n   ";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void embeddedThinkingBeforeUsefulAfterNeitherMostlyThinking() {
        String raw = "x\nThinking Process:\nAnalyze the Request\n\n### 有效中文段落标题\n\n这是足够长的中文回答正文";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("有效中文段落"));
    }

    @Test
    public void dropWhenMostlyThinkingTwoKeywords() {
        String raw = "Analyze the business data and formulate the response without Chinese";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void dropThinkingProcessEmptyExtract() {
        String raw = "Thinking Process:\n\nAnalyze the Request\nDrafting the response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void midBodyThinkingWithoutMarker() {
        String raw = "前缀中文内容足够长可以保留下来作为有效回答";
        assertEquals(raw, LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void thinkingProcessWithInvalidAfterReturnsNull() {
        String raw = "Thinking Process:\n\nstep1\nstep2";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void embeddedThinkingUsefulBeforeShort() {
        String raw = "## 库存报告摘要\n\n这是足够长的中文正文内容\n\nThinking Process:\nAnalyze the Request";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存报告摘要"));
    }

    @Test
    public void mostlyThinkingWithDraftingAndConstraint() {
        String raw = "Drafting the response with constraint 1 details only";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void keepMarkdownTableWithFewChinese() {
        String raw = "|a|b|\n|---|---|\n|1|2|";
        assertNotNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void embeddedThinkingNeitherBeforeNorAfterUseful() {
        String raw = "short\nThinking Process:\nAnalyze the Request\nAnalyze the Business Data\nConstraint 1\nFormulate the Response\nDrafting the response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void thinkingColonPrefixWithoutUsefulExtract() {
        String raw = "Thinking:\n\nAnalyze the Request\nDrafting the response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void embeddedThinkingKeepsOriginalWhenUsefulAndNotMostlyThinking() {
        String raw = "有效中文前缀内容足够长可以保留\nThinking Process:\nAnalyze the Request";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("有效中文前缀"));
    }

    @Test
    public void embeddedThinkingReturnsNullWhenMostlyThinkingAfterBlock() {
        String raw = "短\nThinking Process:\nAnalyze the Request\nAnalyze the Business Data\nConstraint 1\nFormulate the Response\nDrafting the response";
        assertNull(LlmAnswerCleaner.clean(raw));
    }

    @Test
    public void extractAfterFinalConclusionColonMarker() {
        String raw = "Thinking Process:\n\nstep\n\n最终结论：库存周转天数处于合理区间";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("库存周转"));
    }

    @Test
    public void extractAfterHashHeadingMarker() {
        String raw = "Thinking Process:\n\nstep\n\n## 中文分析标题\n\n正文内容足够长可以输出";
        String cleaned = LlmAnswerCleaner.clean(raw);
        assertNotNull(cleaned);
        assertTrue(cleaned.contains("中文分析标题"));
    }

    @Test
    public void hasUsefulAnswerReturnsFalseForNullViaClean() {
        assertNull(LlmAnswerCleaner.clean("Analyze the Request only"));
    }

    @Test
    public void mostlyThinkingExactlyTwoHitsWithoutChinese() {
        String raw = "Analyze the business data and constraint 1 only english";
        assertNull(LlmAnswerCleaner.clean(raw));
    }
}
