package com.ai.client;

import com.ai.config.AiProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容 Chat Completions 客户端（支持自定义 base-url）
 */
@Component
public class OpenAiCompatibleClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleClient.class);

    private final AiProperties aiProperties;

    public OpenAiCompatibleClient(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        String apiKey = aiProperties.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return null;
        }
        String url = resolveUrl();
        int timeout = Math.max(aiProperties.getTimeoutMs(), 500);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("model", aiProperties.getModel());
        // 思考模型若关不掉 thinking，需要更大额度留给最终 content
        body.put("max_tokens", Math.max(aiProperties.getMaxTokens(), 4096));
        body.put("temperature", 0.2);
        body.put("stream", Boolean.FALSE);
        body.put("enable_thinking", Boolean.FALSE);
        Map<String, Object> chatTemplateKwargs = new HashMap<String, Object>();
        chatTemplateKwargs.put("enable_thinking", Boolean.FALSE);
        body.put("chat_template_kwargs", chatTemplateKwargs);
        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        Map<String, String> sys = new HashMap<String, String>();
        sys.put("role", "system");
        sys.put("content", systemPrompt);
        messages.add(sys);
        Map<String, String> user = new HashMap<String, String>();
        user.put("role", "user");
        user.put("content", userPrompt);
        messages.add(user);
        body.put("messages", messages);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "Bearer " + apiKey.trim());
        try {
            post.setEntity(new StringEntity(JSON.toJSONString(body), StandardCharsets.UTF_8));
            CloseableHttpResponse response = client.execute(post);
            try {
                int code = response.getStatusLine().getStatusCode();
                String resp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                if (code < 200 || code >= 300) {
                    log.warn("LLM HTTP {} url={} body={}", code, url, truncate(resp));
                    return null;
                }
                String content = extractContent(resp);
                if (content == null) {
                    log.warn("LLM parse empty, url={} body={}", url, truncate(resp));
                }
                return content;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            log.warn("LLM call failed url={} err={}", url, e.toString());
            return null;
        } finally {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    private String resolveUrl() {
        String baseUrl = aiProperties.getBaseUrl();
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            String base = baseUrl.trim();
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            if (base.endsWith("/chat/completions")) {
                return base;
            }
            return base + "/chat/completions";
        }
        String provider = aiProperties.getProvider() == null ? "" : aiProperties.getProvider().toLowerCase();
        if ("deepseek".equals(provider)) {
            return "https://api.deepseek.com/chat/completions";
        }
        if ("dashscope".equals(provider)) {
            return "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        }
        return "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    }

    private String extractContent(String resp) {
        JSONObject obj = JSON.parseObject(resp);
        if (obj == null) {
            return null;
        }
        JSONArray choices = obj.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            log.warn("LLM response missing choices: {}", truncate(resp));
            return null;
        }
        JSONObject choice0 = choices.getJSONObject(0);
        if (choice0 == null) {
            return null;
        }
        JSONObject msg = choice0.getJSONObject("message");
        if (msg == null) {
            // 部分兼容接口把文本放在 text 字段
            String text = choice0.getString("text");
            return text == null || text.trim().isEmpty() ? null : text.trim();
        }
        Object content = msg.get("content");
        String main = coalesceText(content);
        if (main != null && !main.isEmpty()) {
            return main;
        }
        // 部分中转在关闭思考失败时，只把全文塞进 reasoning_content
        String reasoning = msg.getString("reasoning_content");
        if (reasoning != null && !reasoning.trim().isEmpty()) {
            log.info("LLM content empty, using reasoning_content for post-clean, len={}", reasoning.length());
            return reasoning.trim();
        }
        return null;
    }

    private String coalesceText(Object content) {
        if (content == null || "null".equals(String.valueOf(content))) {
            return null;
        }
        if (content instanceof String) {
            String s = ((String) content).trim();
            return s.isEmpty() ? null : s;
        }
        if (content instanceof JSONArray) {
            JSONArray arr = (JSONArray) content;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.size(); i++) {
                Object item = arr.get(i);
                if (item instanceof String) {
                    sb.append(item);
                } else if (item instanceof JSONObject) {
                    JSONObject part = (JSONObject) item;
                    String type = part.getString("type");
                    if (type != null && type.toLowerCase().contains("reason")) {
                        continue;
                    }
                    String t = part.getString("text");
                    if (t == null) {
                        t = part.getString("content");
                    }
                    if (t != null) {
                        sb.append(t);
                    }
                }
            }
            String s = sb.toString().trim();
            return s.isEmpty() ? null : s;
        }
        String s = String.valueOf(content).trim();
        return s.isEmpty() ? null : s;
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 300 ? text : text.substring(0, 300) + "...";
    }
}
