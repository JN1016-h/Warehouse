package com.ai.dto;

/**
 * 对话风格（FR9）
 */
public enum AiStyle {
    SIMPLE,
    DETAILED;

    public static AiStyle from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SIMPLE;
        }
        String v = value.trim().toUpperCase();
        if ("DETAILED".equals(v) || "详细".equals(value) || "详细专业".equals(value)) {
            return DETAILED;
        }
        return SIMPLE;
    }

    public String displayName() {
        return this == DETAILED ? "详细专业仓储分析" : "简洁报表";
    }
}
