package com.ai.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 清洗大模型输出：去掉 Thinking / 思考过程，只保留最终回答。
 */
public final class LlmAnswerCleaner {

    private static final Pattern THINK_TAG = Pattern.compile(
            "(?is)<\\s*(think|thinking)\\s*>.*?<\\s*/\\s*\\1\\s*>");
    private static final Pattern REDACTED_THINK = Pattern.compile(
            "(?is)redacted_thinking.*?/redacted_thinking");

    private LlmAnswerCleaner() {
    }

    public static String clean(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }

        s = THINK_TAG.matcher(s).replaceAll("");
        s = REDACTED_THINK.matcher(s).replaceAll("");
        s = s.replaceAll("(?is)```\\s*thinking\\s*[\\s\\S]*?```", "");

        // 整段都是 Thinking Process 且没有可用中文结论 → 视为无效
        String lower = s.toLowerCase();
        if (lower.startsWith("thinking process") || lower.startsWith("thinking:")) {
            String after = extractAfterThinking(s);
            if (after != null && !after.trim().isEmpty() && hasUsefulAnswer(after)) {
                s = after.trim();
            } else {
                return null;
            }
        } else if (lower.contains("thinking process:")) {
            // 正文前夹杂思考：尝试截取思考块之后
            int idx = lower.indexOf("thinking process:");
            if (idx >= 0) {
                String before = s.substring(0, idx).trim();
                String after = extractAfterThinking(s.substring(idx));
                if (hasUsefulAnswer(before)) {
                    s = before;
                } else if (after != null && hasUsefulAnswer(after)) {
                    s = after.trim();
                } else if (!hasUsefulAnswer(s) || isMostlyThinking(s)) {
                    return null;
                }
            }
        }

        s = s.trim();
        if (s.isEmpty() || isMostlyThinking(s) || !hasUsefulAnswer(s)) {
            return null;
        }
        return s;
    }

    private static String extractAfterThinking(String thinkingBlock) {
        String[] markers = {
                "最终回答", "最终结论", "Final Answer", "Final answer",
                "## ", "### ", "结论：", "结论:", "回答："
        };
        String lower = thinkingBlock.toLowerCase();
        int best = -1;
        int markerLen = 0;
        for (String m : markers) {
            int i = thinkingBlock.indexOf(m);
            if (i < 0) {
                i = lower.indexOf(m.toLowerCase());
            }
            if (i >= 0 && (best < 0 || i < best)) {
                best = i;
                markerLen = m.length();
            }
        }
        if (best >= 0) {
            return thinkingBlock.substring(best + markerLen).trim();
        }
        // 找第一段中文 Markdown 标题/列表作为回答起点
        Matcher zh = Pattern.compile("(?m)^\\s*(#{1,3}\\s*[\u4e00-\u9fff].*|[-*]\\s*[\u4e00-\u9fff].*|[\u4e00-\u9fff].{8,})$")
                .matcher(thinkingBlock);
        if (zh.find()) {
            return thinkingBlock.substring(zh.start()).trim();
        }
        return null;
    }

    private static boolean isMostlyThinking(String s) {
        String lower = s.toLowerCase();
        int hits = 0;
        if (lower.contains("thinking process")) hits++;
        if (lower.contains("analyze the request")) hits++;
        if (lower.contains("analyze the business data")) hits++;
        if (lower.contains("formulate the response")) hits++;
        if (lower.contains("constraint 1")) hits++;
        if (lower.contains("drafting the response")) hits++;
        return hits >= 2;
    }

    private static boolean hasUsefulAnswer(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        // 至少包含一定中文，避免只留下英文思考残片
        int zh = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x4e00 && c <= 0x9fff) {
                zh++;
                if (zh >= 6) {
                    return true;
                }
            }
        }
        return s.contains("|---|") || s.contains("暂无相关库存统计数据");
    }
}
