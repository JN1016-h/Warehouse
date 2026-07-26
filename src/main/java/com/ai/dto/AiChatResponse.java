package com.ai.dto;

import java.util.Map;

/**
 * 聊天响应
 */
public class AiChatResponse {
    private Long sessionId;
    private Long messageId;
    private String answer;
    private String intent;
    private boolean degraded;
    private boolean cached;
    private boolean denied;
    private Map<String, Object> data;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public boolean isDegraded() {
        return degraded;
    }

    public void setDegraded(boolean degraded) {
        this.degraded = degraded;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
