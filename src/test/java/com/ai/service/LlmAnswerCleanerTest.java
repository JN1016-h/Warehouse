package com.ai.service;

import org.junit.jupiter.api.Test;

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
}
