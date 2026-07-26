package com.ai.service;

import com.ai.cache.AiResultCache;
import com.ai.dto.AiChatRequest;
import com.ai.dto.AiChatResponse;
import com.ai.dto.AiIntent;
import com.ai.dto.AiStyle;
import com.ai.dto.TimeRange;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AI 对话主流程：鉴权域 → 意图 → 本地统计 → 缓存 → LLM/降级 → 落库
 */
@Service
public class AiChatService {

    private final IntentRouter intentRouter;
    private final TimeRangeResolver timeRangeResolver;
    private final AnalyticsFacade analyticsFacade;
    private final LlmGateway llmGateway;
    private final AiResultCache aiResultCache;
    private final ChatHistoryService chatHistoryService;

    public AiChatService(IntentRouter intentRouter,
                         TimeRangeResolver timeRangeResolver,
                         AnalyticsFacade analyticsFacade,
                         LlmGateway llmGateway,
                         AiResultCache aiResultCache,
                         ChatHistoryService chatHistoryService) {
        this.intentRouter = intentRouter;
        this.timeRangeResolver = timeRangeResolver;
        this.analyticsFacade = analyticsFacade;
        this.llmGateway = llmGateway;
        this.aiResultCache = aiResultCache;
        this.chatHistoryService = chatHistoryService;
    }

    public AiChatResponse chat(AiChatRequest request, DataScopeResolver.ResolvedUser user) {
        AiChatResponse resp = new AiChatResponse();
        if (user == null || DataScopeResolver.SCOPE_DENIED.equals(user.dataScope)) {
            resp.setDenied(true);
            resp.setAnswer("当前角色（经销商）未开放 AI 智能分析助手。");
            resp.setDegraded(true);
            return resp;
        }

        String question = request.getQuestion() == null ? "" : request.getQuestion().trim();
        if (question.isEmpty()) {
            resp.setAnswer("请输入要查询的问题。");
            return resp;
        }

        AiStyle style = AiStyle.from(request.getStyle());
        TimeRange range = timeRangeResolver.resolve(request.getTimeRange(), request.getStartDate(), request.getEndDate());
        AiIntent intent = intentRouter.route(question);
        boolean financeOk = DataScopeResolver.SCOPE_FINANCE_OK.equals(user.dataScope);

        if (intent == AiIntent.FINANCE && !financeOk) {
            resp.setDenied(true);
            resp.setIntent(intent.name());
            resp.setAnswer("当前角色无财务数据权限，无法查询应收应付/回款类信息。");
            AiChatSessionEntity session = chatHistoryService.getOrCreateSession(
                    request.getSessionId(), user.userId, user.tableName, style.name(), question);
            chatHistoryService.saveMessage(session.getId(), "user", question, null, false, intent.name());
            AiChatMessageEntity assistant = chatHistoryService.saveMessage(
                    session.getId(), "assistant", resp.getAnswer(), null, true, intent.name());
            resp.setSessionId(session.getId());
            resp.setMessageId(assistant.getId());
            return resp;
        }

        Map<String, Object> data = analyticsFacade.assemble(intent, range, financeOk);
        String cacheKey = buildCacheKey(intent, style, user.dataScope, range, question);
        String cachedAnswer = aiResultCache.get(cacheKey);

        String answer;
        boolean degraded;
        boolean cached = false;
        if (cachedAnswer != null) {
            answer = cachedAnswer;
            degraded = false;
            cached = true;
        } else {
            LlmGateway.Result result = llmGateway.generate(question, style, user.dataScope, data);
            answer = result.answer;
            degraded = result.degraded;
            // 仅缓存在线模型成功回答，避免把降级结果锁进缓存
            if (!degraded && answer != null) {
                aiResultCache.put(cacheKey, answer);
            }
        }

        String snapshot = JSON.toJSONString(data);
        AiChatSessionEntity session = chatHistoryService.getOrCreateSession(
                request.getSessionId(), user.userId, user.tableName, style.name(), question);
        chatHistoryService.saveMessage(session.getId(), "user", question, null, false, intent.name());
        AiChatMessageEntity assistant = chatHistoryService.saveMessage(
                session.getId(), "assistant", answer, snapshot, degraded, intent.name());

        resp.setSessionId(session.getId());
        resp.setMessageId(assistant.getId());
        resp.setAnswer(answer);
        resp.setIntent(intent.name());
        resp.setDegraded(degraded);
        resp.setCached(cached);
        resp.setData(data);
        return resp;
    }

    public Map<String, Object> preview(AiChatRequest request, DataScopeResolver.ResolvedUser user) {
        if (user == null || DataScopeResolver.SCOPE_DENIED.equals(user.dataScope)) {
            throw new IllegalStateException("当前角色未开放 AI 分析");
        }
        AiIntent intent = intentRouter.route(request.getQuestion());
        TimeRange range = timeRangeResolver.resolve(request.getTimeRange(), request.getStartDate(), request.getEndDate());
        boolean financeOk = DataScopeResolver.SCOPE_FINANCE_OK.equals(user.dataScope);
        if (intent == AiIntent.FINANCE && !financeOk) {
            throw new IllegalStateException("无财务数据权限");
        }
        return analyticsFacade.assemble(intent, range, financeOk);
    }

    public String exportReport(Long messageId, Long userId, String format) {
        AiChatMessageEntity msg = chatHistoryService.getMessage(messageId, userId);
        if (msg == null) {
            return null;
        }
        String content = msg.getContent() == null ? "" : msg.getContent();
        String snapshot = msg.getDataSnapshot() == null ? "" : msg.getDataSnapshot();
        if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("role,intent,degraded,content\n");
            csv.append("assistant,").append(msg.getIntent()).append(",")
                    .append(msg.getDegraded()).append(",\"")
                    .append(content.replace("\"", "\"\"")).append("\"\n");
            csv.append("\ndata_snapshot\n\"").append(snapshot.replace("\"", "\"\"")).append("\"\n");
            return csv.toString();
        }
        StringBuilder md = new StringBuilder();
        md.append("# AI 分析报告\n\n");
        md.append("- 意图：").append(msg.getIntent()).append("\n");
        md.append("- 是否降级：").append(msg.getDegraded() != null && msg.getDegraded() == 1).append("\n\n");
        md.append("## 结论\n\n").append(content).append("\n\n");
        md.append("## 指标快照\n\n```json\n").append(snapshot).append("\n```\n");
        return md.toString();
    }

    private String buildCacheKey(AiIntent intent, AiStyle style, String scope, TimeRange range, String question) {
        String raw = intent.name() + "|" + style.name() + "|" + scope + "|"
                + range.getLabel() + "|" + range.getStart() + "|" + range.getEnd() + "|" + question;
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
