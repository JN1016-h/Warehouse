package com.ai.service;

import com.ai.client.LlmClient;
import com.ai.config.AiProperties;
import com.ai.dto.AiStyle;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 在线 LLM 网关：超时/失败走 FallbackResponder
 */
@Service
public class LlmGateway {

    private final AiProperties aiProperties;
    private final LlmClient llmClient;
    private final FallbackResponder fallbackResponder;

    public LlmGateway(AiProperties aiProperties, LlmClient llmClient, FallbackResponder fallbackResponder) {
        this.aiProperties = aiProperties;
        this.llmClient = llmClient;
        this.fallbackResponder = fallbackResponder;
    }

    public Result generate(String question, AiStyle style, String dataScope, Map<String, Object> data) {
        if (Boolean.TRUE.equals(data.get("permissionDenied"))) {
            Result r = new Result();
            r.answer = String.valueOf(data.get("permissionMessage"));
            r.degraded = true;
            return r;
        }
        if (data == null || Boolean.FALSE.equals(data.get("hasData"))) {
            Result r = new Result();
            r.answer = "暂无相关库存统计数据";
            r.degraded = true;
            return r;
        }

        boolean canCallLlm = aiProperties.isEnabled()
                && aiProperties.getApiKey() != null
                && !aiProperties.getApiKey().trim().isEmpty();

        if (!canCallLlm) {
            org.slf4j.LoggerFactory.getLogger(LlmGateway.class)
                    .warn("LLM skipped: enabled={}, apiKeyEmpty={}",
                            aiProperties.isEnabled(),
                            aiProperties.getApiKey() == null || aiProperties.getApiKey().trim().isEmpty());
            Result r = new Result();
            r.answer = fallbackResponder.respond(question, style, data, "未配置 API Key，以下结论基于业务库聚合结果。");
            r.degraded = true;
            return r;
        }

        String system = buildSystemPrompt(style, dataScope);
        String user = "业务数据（JSON）：\n" + JSON.toJSONString(data)
                + "\n\n用户提问：\n" + question
                + "\n\n要求：直接输出最终中文结论与表格，不要输出 Thinking Process / 思考过程。";
        String raw = llmClient.chat(system, user);
        String answer = LlmAnswerCleaner.clean(raw);
        Result r = new Result();
        if (answer == null || answer.trim().isEmpty()) {
            org.slf4j.LoggerFactory.getLogger(LlmGateway.class)
                    .warn("LLM empty/thinking-only after clean, fallback. rawLen={}, provider={}, model={}",
                            raw == null ? 0 : raw.length(),
                            aiProperties.getProvider(), aiProperties.getModel());
            String reason = (raw != null && raw.toLowerCase().contains("thinking"))
                    ? "模型仅返回思考过程未形成有效结论，已改用本地统计结果。"
                    : "模型未返回有效结论，已改用本地统计结果。";
            r.answer = fallbackResponder.respond(question, style, data, reason);
            r.degraded = true;
        } else {
            r.answer = answer.trim();
            r.degraded = false;
        }
        return r;
    }

    private String buildSystemPrompt(AiStyle style, String dataScope) {
        return "你是专业仓储数据分析助手。你只能基于下方「业务数据」回答，规则：\n"
                + "1. 禁止编造任何库存、出库、金额数字；业务数据中没有的内容，回复「暂无相关库存统计数据」；\n"
                + "2. 分析动销、库存风险、补货建议时分点输出，条理清晰；\n"
                + "3. 用户需要报表时，用 Markdown 表格输出；\n"
                + "4. 当前风格：" + style.displayName() + "\n"
                + "5. 当前用户数据权限：" + dataScope + "\n"
                + "6. 若用户问题超出其数据权限，明确告知无权限，不要猜测财务数据；\n"
                + "7. 直接输出最终中文结论，禁止输出 Thinking Process、思考过程、推理步骤、英文分析草稿。";
    }

    public static class Result {
        public String answer;
        public boolean degraded;
    }
}
