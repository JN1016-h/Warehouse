package com.ai.service;

import com.ai.config.AiProperties;
import com.ai.dto.AiIntent;
import com.ai.dto.AiStyle;
import com.ai.dto.TimeRange;
import com.ai.client.LlmClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LlmGatewayTest {

    @Mock
    private AiProperties aiProperties;

    @Mock
    private LlmClient llmClient;

    @Mock
    private FallbackResponder fallbackResponder;

    @InjectMocks
    private LlmGateway llmGateway;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generatePermissionDenied() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("permissionDenied", true);
        data.put("permissionMessage", "无权限");

        LlmGateway.Result result = llmGateway.generate("财务", AiStyle.SIMPLE, "WAREHOUSE", data);

        assertTrue(result.degraded);
        assertEquals("无权限", result.answer);
        verify(llmClient, never()).chat(anyString(), anyString());
    }

    @Test
    public void generateNoData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("hasData", false);

        LlmGateway.Result result = llmGateway.generate("库存", AiStyle.SIMPLE, "FINANCE_OK", data);

        assertTrue(result.degraded);
        assertEquals("暂无相关库存统计数据", result.answer);
    }

    @Test
    public void generateNullData() {
        LlmGateway.Result result = llmGateway.generate("库存", AiStyle.SIMPLE, "FINANCE_OK", null);

        assertTrue(result.degraded);
        assertEquals("暂无相关库存统计数据", result.answer);
    }

    @Test
    public void generateNoApiKeyUsesFallback() {
        Map<String, Object> data = sampleData();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("");
        when(fallbackResponder.respond(eq("问题"), eq(AiStyle.SIMPLE), eq(data), anyString()))
                .thenReturn("本地统计回答");

        LlmGateway.Result result = llmGateway.generate("问题", AiStyle.SIMPLE, "FINANCE_OK", data);

        assertTrue(result.degraded);
        assertEquals("本地统计回答", result.answer);
        verify(llmClient, never()).chat(anyString(), anyString());
    }

    @Test
    public void generateDisabledUsesFallback() {
        Map<String, Object> data = sampleData();
        when(aiProperties.isEnabled()).thenReturn(false);
        when(aiProperties.getApiKey()).thenReturn("sk-test");
        when(fallbackResponder.respond(anyString(), any(), any(), anyString())).thenReturn("降级");

        LlmGateway.Result result = llmGateway.generate("问题", AiStyle.DETAILED, "FINANCE_OK", data);

        assertTrue(result.degraded);
        assertEquals("降级", result.answer);
    }

    @Test
    public void generateLlmSuccess() {
        Map<String, Object> data = sampleData();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("sk-test");
        when(llmClient.chat(anyString(), anyString())).thenReturn("## 分析结论\n\n库存正常");

        LlmGateway.Result result = llmGateway.generate("库存如何", AiStyle.SIMPLE, "FINANCE_OK", data);

        assertFalse(result.degraded);
        assertTrue(result.answer.contains("分析结论"));
    }

    @Test
    public void generateLlmEmptyFallsBack() {
        Map<String, Object> data = sampleData();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("sk-test");
        when(aiProperties.getProvider()).thenReturn("dashscope");
        when(aiProperties.getModel()).thenReturn("qwen");
        when(llmClient.chat(anyString(), anyString())).thenReturn("Thinking Process:\nAnalyze...");
        when(fallbackResponder.respond(anyString(), any(), any(), contains("思考过程")))
                .thenReturn("降级本地");

        LlmGateway.Result result = llmGateway.generate("动销", AiStyle.SIMPLE, "FINANCE_OK", data);

        assertTrue(result.degraded);
        assertEquals("降级本地", result.answer);
    }

    @Test
    public void generateLlmNullFallsBackGenericReason() {
        Map<String, Object> data = sampleData();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("sk-test");
        when(aiProperties.getProvider()).thenReturn("dashscope");
        when(aiProperties.getModel()).thenReturn("qwen");
        when(llmClient.chat(anyString(), anyString())).thenReturn(null);
        when(fallbackResponder.respond(anyString(), any(), any(), contains("未返回有效结论")))
                .thenReturn("降级");

        LlmGateway.Result result = llmGateway.generate("周转", AiStyle.SIMPLE, "FINANCE_OK", data);

        assertTrue(result.degraded);
        assertEquals("降级", result.answer);
    }

    private Map<String, Object> sampleData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("hasData", true);
        data.put("timeRange", "近月");
        return data;
    }
}
