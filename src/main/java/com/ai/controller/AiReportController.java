package com.ai.controller;

import com.ai.service.AiChatService;
import com.ai.service.DataScopeResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * AI 报告导出（FR7）
 */
@RestController
@RequestMapping("/ai/report")
public class AiReportController {

    private final AiChatService aiChatService;
    private final DataScopeResolver dataScopeResolver;

    public AiReportController(AiChatService aiChatService, DataScopeResolver dataScopeResolver) {
        this.aiChatService = aiChatService;
        this.dataScopeResolver = dataScopeResolver;
    }

    @PostMapping("/export")
    public void export(@RequestBody Map<String, Object> body,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        try {
            DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);
            if (!dataScopeResolver.canUseAi(user.dataScope)) {
                writeJsonError(response, 403, "当前角色未开放 AI 助手");
                return;
            }
            Object mid = body.get("messageId");
            if (mid == null) {
                writeJsonError(response, 400, "messageId 不能为空");
                return;
            }
            Long messageId = Long.valueOf(String.valueOf(mid));
            String format = body.get("format") == null ? "md" : String.valueOf(body.get("format"));
            String content = aiChatService.exportReport(messageId, user.userId, format);
            if (content == null) {
                writeJsonError(response, 404, "消息不存在或无权限");
                return;
            }
            boolean csv = "csv".equalsIgnoreCase(format);
            String filename = csv ? "ai-report.csv" : "ai-report.md";
            String contentType = csv ? "text/csv;charset=UTF-8" : "text/markdown;charset=UTF-8";
            response.setContentType(contentType);
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            response.setContentLength(bytes.length);
            OutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                writeJsonError(response, 500, "导出失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    private void writeJsonError(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"code\":" + code + ",\"msg\":\"" + msg.replace("\"", "'") + "\"}";
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }
}
