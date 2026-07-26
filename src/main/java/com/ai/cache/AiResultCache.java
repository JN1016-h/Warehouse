package com.ai.cache;

import com.ai.config.AiProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 结构化分析结果缓存（NFR2）
 */
@Component
public class AiResultCache {

    private final AiProperties aiProperties;
    private Cache<String, String> cache;

    public AiResultCache(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(aiProperties.getCache().getMaxSize())
                .expireAfterWrite(aiProperties.getCache().getTtlMinutes(), TimeUnit.MINUTES)
                .build();
    }

    public String get(String key) {
        return cache.getIfPresent(key);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            cache.put(key, value);
        }
    }
}
