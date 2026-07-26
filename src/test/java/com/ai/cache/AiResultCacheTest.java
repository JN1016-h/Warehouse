package com.ai.cache;

import com.ai.config.AiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AiResultCacheTest {

    private AiResultCache cache;

    @BeforeEach
    public void setUp() {
        AiProperties props = new AiProperties();
        AiProperties.Cache cacheConfig = new AiProperties.Cache();
        cacheConfig.setMaxSize(100);
        cacheConfig.setTtlMinutes(30);
        props.setCache(cacheConfig);
        cache = new AiResultCache(props);
        cache.init();
    }

    @Test
    public void putAndGet() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    public void getMissingKeyReturnsNull() {
        assertNull(cache.get("nonexistent"));
    }

    @Test
    public void putIgnoresNullKey() {
        cache.put(null, "value");
        assertNull(cache.get("any-key-not-null"));
    }

    @Test
    public void putIgnoresNullValue() {
        cache.put("key", null);
        assertNull(cache.get("key"));
    }

    @Test
    public void putOverwritesExisting() {
        cache.put("key", "first");
        cache.put("key", "second");
        assertEquals("second", cache.get("key"));
    }

    @Test
    public void expireAfterTtl() throws InterruptedException {
        AiProperties props = new AiProperties();
        AiProperties.Cache cacheConfig = new AiProperties.Cache();
        cacheConfig.setMaxSize(10);
        cacheConfig.setTtlMinutes(0);
        props.setCache(cacheConfig);
        AiResultCache shortTtl = new AiResultCache(props);
        shortTtl.init();
        shortTtl.put("expiring", "data");
        Thread.sleep(50);
        assertNull(shortTtl.get("expiring"));
    }
}
