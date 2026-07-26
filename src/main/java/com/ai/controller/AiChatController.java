package com.ai.controller;

import com.ai.dto.AiChatRequest;
import com.ai.dto.AiChatResponse;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import com.ai.service.AiChatService;
import com.ai.service.ChatHistoryService;
import com.ai.service.DataScopeResolver;
import com.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * AI 对话与历史接口
 */
@RestController
@RequestMapping("/ai")
public class AiChatController {

    private final AiChatService aiChatService;
    private final ChatHistoryService chatHistoryService;
    private final DataScopeResolver dataScopeResolver;

    public AiChatController(AiChatService aiChatService,
                            ChatHistoryService chatHistoryService,
                            DataScopeResolver dataScopeResolver) {
        this.aiChatService = aiChatService;
        this.chatHistoryService = chatHistoryService;
        this.dataScopeResolver = dataScopeResolver;
    }

    @PostMapping("/chat")
    public R chat(@RequestBody AiChatRequest request, HttpServletRequest httpRequest) {
        try {
            DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(httpRequest);
            AiChatResponse response = aiChatService.chat(request, user);
            return R.ok().put("data", response);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("AI 问答失败：" + e.getMessage());
        }
    }

    @GetMapping("/sessions")
    public R sessions(HttpServletRequest httpRequest) {
        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(httpRequest);
        if (!dataScopeResolver.canUseAi(user.dataScope)) {
            return R.error(403, "当前角色未开放 AI 助手");
        }
        List<AiChatSessionEntity> list = chatHistoryService.listSessions(user.userId);
        return R.ok().put("data", list);
    }

    @GetMapping("/sessions/{id}/messages")
    public R messages(@PathVariable("id") Long id, HttpServletRequest httpRequest) {
        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(httpRequest);
        if (!dataScopeResolver.canUseAi(user.dataScope)) {
            return R.error(403, "当前角色未开放 AI 助手");
        }
        List<AiChatMessageEntity> list = chatHistoryService.listMessages(id, user.userId);
        return R.ok().put("data", list);
    }

    @GetMapping("/stats/preview")
    public R preview(@RequestParam Map<String, Object> params, HttpServletRequest httpRequest) {
        try {
            DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(httpRequest);
            if (!dataScopeResolver.canUseAi(user.dataScope)) {
                return R.error(403, "当前角色未开放 AI 助手");
            }
            AiChatRequest request = new AiChatRequest();
            request.setQuestion(params.get("question") == null ? "" : String.valueOf(params.get("question")));
            request.setTimeRange(params.get("timeRange") == null ? "MONTH" : String.valueOf(params.get("timeRange")));
            request.setStartDate(params.get("startDate") == null ? null : String.valueOf(params.get("startDate")));
            request.setEndDate(params.get("endDate") == null ? null : String.valueOf(params.get("endDate")));
            Map<String, Object> data = aiChatService.preview(request, user);
            return R.ok().put("data", data);
        } catch (IllegalStateException ex) {
            return R.error(403, ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("预览统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/scope")
    public R scope(HttpServletRequest httpRequest) {
        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(httpRequest);
        return R.ok()
                .put("dataScope", user.dataScope)
                .put("role", user.role == null ? null : user.role.name())
                .put("canUseAi", dataScopeResolver.canUseAi(user.dataScope))
                .put("canAccessFinance", dataScopeResolver.canAccessFinance(user.dataScope));
    }
}
