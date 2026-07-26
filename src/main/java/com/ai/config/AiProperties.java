package com.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 助手配置
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private boolean enabled = true;
    private String provider = "openai-compatible";
    /** OpenAI 兼容基础地址，如 https://xxx/v1 */
    private String baseUrl = "";
    private String apiKey = "";
    private String model = "qwen-turbo";
    private int timeoutMs = 2500;
    private int maxTokens = 1200;
    private int topN = 20;
    private Replenish replenish = new Replenish();
    private Cache cache = new Cache();

    public static class Replenish {
        private int defaultLeadDays = 14;

        public int getDefaultLeadDays() {
            return defaultLeadDays;
        }

        public void setDefaultLeadDays(int defaultLeadDays) {
            this.defaultLeadDays = defaultLeadDays;
        }
    }

    public static class Cache {
        private int ttlMinutes = 30;
        private int maxSize = 500;

        public int getTtlMinutes() {
            return ttlMinutes;
        }

        public void setTtlMinutes(int ttlMinutes) {
            this.ttlMinutes = ttlMinutes;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public Replenish getReplenish() {
        return replenish;
    }

    public void setReplenish(Replenish replenish) {
        this.replenish = replenish;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
