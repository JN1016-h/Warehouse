package com.ai.client;

/**
 * 大模型客户端接口
 */
public interface LlmClient {

    /**
     * @return 模型文本；失败返回 null
     */
    String chat(String systemPrompt, String userPrompt);
}
