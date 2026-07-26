package com.ai.client;

import com.ai.config.AiProperties;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class OpenAiCompatibleClientTest {

    private AiProperties aiProperties;
    private OpenAiCompatibleClient client;
    private HttpServer mockServer;

    @BeforeEach
    public void setUp() {
        aiProperties = new AiProperties();
        client = new OpenAiCompatibleClient(aiProperties);
    }

    @AfterEach
    public void tearDown() {
        if (mockServer != null) {
            mockServer.stop(0);
            mockServer = null;
        }
    }

    private void startMockServer(String responseBody, int statusCode) throws Exception {
        mockServer = HttpServer.create(new InetSocketAddress(0), 0);
        mockServer.createContext("/v1/chat/completions", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });
        mockServer.start();
        aiProperties.setBaseUrl("http://127.0.0.1:" + mockServer.getAddress().getPort() + "/v1");
        aiProperties.setApiKey("test-api-key");
    }

    @Test
    public void chatSuccessReturnsContent() throws Exception {
        startMockServer("{\"choices\":[{\"message\":{\"content\":\"mock answer\"}}]}", 200);
        assertEquals("mock answer", client.chat("system", "user"));
    }

    @Test
    public void chatHttpErrorReturnsNull() throws Exception {
        startMockServer("{\"error\":\"rate limit\"}", 429);
        assertNull(client.chat("system", "user"));
    }

    @Test
    public void chatEmptyChoicesReturnsNull() throws Exception {
        startMockServer("{\"choices\":[]}", 200);
        assertNull(client.chat("system", "user"));
    }

    @Test
    public void chatInvalidUrlReturnsNull() {
        aiProperties.setApiKey("key");
        aiProperties.setBaseUrl("http://127.0.0.1:1/v1");
        aiProperties.setTimeoutMs(500);
        assertNull(client.chat("system", "user"));
    }

    @Test
    public void chatUsesMinimumTimeout() throws Exception {
        startMockServer("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}", 200);
        aiProperties.setTimeoutMs(100);
        assertEquals("ok", client.chat("system", "user"));
    }

    @Test
    public void chatReturnsNullWhenApiKeyEmpty() {
        aiProperties.setApiKey("");
        assertNull(client.chat("sys", "user"));

        aiProperties.setApiKey(null);
        assertNull(client.chat("sys", "user"));

        aiProperties.setApiKey("   ");
        assertNull(client.chat("sys", "user"));
    }

    @Test
    public void resolveUrlUsesBaseUrl() throws Exception {
        aiProperties.setBaseUrl("https://api.example.com/v1/");
        assertEquals("https://api.example.com/v1/chat/completions", invokeResolveUrl());

        aiProperties.setBaseUrl("https://api.example.com/v1/chat/completions");
        assertEquals("https://api.example.com/v1/chat/completions", invokeResolveUrl());
    }

    @Test
    public void resolveUrlUsesProviderDeepseek() throws Exception {
        aiProperties.setBaseUrl("");
        aiProperties.setProvider("deepseek");
        assertEquals("https://api.deepseek.com/chat/completions", invokeResolveUrl());
    }

    @Test
    public void resolveUrlUsesProviderDashscope() throws Exception {
        aiProperties.setBaseUrl("");
        aiProperties.setProvider("dashscope");
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", invokeResolveUrl());
    }

    @Test
    public void resolveUrlDefaultDashscope() throws Exception {
        aiProperties.setBaseUrl("");
        aiProperties.setProvider("unknown");
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", invokeResolveUrl());
    }

    @Test
    public void extractContentFromMessage() throws Exception {
        String json = "{\"choices\":[{\"message\":{\"content\":\"最终回答内容\"}}]}";
        assertEquals("最终回答内容", invokeExtractContent(json));
    }

    @Test
    public void extractContentFromTextField() throws Exception {
        String json = "{\"choices\":[{\"text\":\"文本字段回答\"}]}";
        assertEquals("文本字段回答", invokeExtractContent(json));
    }

    @Test
    public void extractContentFromReasoningContent() throws Exception {
        String json = "{\"choices\":[{\"message\":{\"content\":\"\",\"reasoning_content\":\"推理后的中文结论内容足够长\"}}]}";
        assertEquals("推理后的中文结论内容足够长", invokeExtractContent(json));
    }

    @Test
    public void extractContentMissingChoices() throws Exception {
        assertNull(invokeExtractContent("{\"choices\":[]}"));
        assertNull(invokeExtractContent("{}"));
        assertNull(invokeExtractContent("{\"foo\":1}"));
    }

    @Test
    public void coalesceTextHandlesStringAndArray() throws Exception {
        assertEquals("hello", invokeCoalesceText("hello"));
        assertNull(invokeCoalesceText(""));
        assertNull(invokeCoalesceText("null"));

        String arrayJson = "[{\"type\":\"reasoning\",\"text\":\"skip\"},{\"type\":\"text\",\"text\":\"part1\"},{\"text\":\"part2\"}]";
        assertEquals("part1part2", invokeCoalesceText(com.alibaba.fastjson.JSON.parse(arrayJson)));
    }

    @Test
    public void truncateLongText() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 400; i++) {
            sb.append('x');
        }
        String truncated = invokeTruncate(sb.toString());
        assertTrue(truncated.endsWith("..."));
        assertTrue(truncated.length() < sb.length());
        assertEquals("", invokeTruncate(null));
    }

    @Test
    public void truncateShortTextUnchanged() throws Exception {
        assertEquals("short", invokeTruncate("short"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('x');
        }
        assertEquals(sb.toString(), invokeTruncate(sb.toString()));
    }

    @Test
    public void extractContentNullChoice() throws Exception {
        assertNull(invokeExtractContent("{\"choices\":[null]}"));
    }

    @Test
    public void extractContentEmptyTextField() throws Exception {
        assertNull(invokeExtractContent("{\"choices\":[{\"text\":\"   \"}]}"));
    }

    @Test
    public void extractContentWhitespaceOnlyMessage() throws Exception {
        assertNull(invokeExtractContent("{\"choices\":[{\"message\":{\"content\":\"   \"}}]}"));
    }

    @Test
    public void extractContentNullJson() throws Exception {
        assertNull(invokeExtractContent("null"));
    }

    @Test
    public void coalesceTextJsonObjectViaArray() throws Exception {
        com.alibaba.fastjson.JSONObject part = new com.alibaba.fastjson.JSONObject();
        part.put("type", "text");
        part.put("content", "from-content-field");
        com.alibaba.fastjson.JSONArray arr = new com.alibaba.fastjson.JSONArray();
        arr.add(part);
        assertEquals("from-content-field", invokeCoalesceText(arr));
    }

    @Test
    public void coalesceTextJsonObjectTextFieldViaArray() throws Exception {
        com.alibaba.fastjson.JSONObject part = new com.alibaba.fastjson.JSONObject();
        part.put("type", "output");
        part.put("text", "from-text-field");
        com.alibaba.fastjson.JSONArray arr = new com.alibaba.fastjson.JSONArray();
        arr.add(part);
        assertEquals("from-text-field", invokeCoalesceText(arr));
    }

    @Test
    public void coalesceTextNonStringScalar() throws Exception {
        assertEquals("42", invokeCoalesceText(Integer.valueOf(42)));
    }

    @Test
    public void coalesceTextArrayWithStringItems() throws Exception {
        com.alibaba.fastjson.JSONArray arr = new com.alibaba.fastjson.JSONArray();
        arr.add("partA");
        arr.add("partB");
        assertEquals("partApartB", invokeCoalesceText(arr));
    }

    @Test
    public void coalesceTextArrayEmptyAfterSkip() throws Exception {
        String arrayJson = "[{\"type\":\"reasoning\",\"text\":\"skip-only\"}]";
        assertNull(invokeCoalesceText(com.alibaba.fastjson.JSON.parse(arrayJson)));
    }

    @Test
    public void resolveUrlNullBaseUrl() throws Exception {
        aiProperties.setBaseUrl(null);
        aiProperties.setProvider("deepseek");
        assertEquals("https://api.deepseek.com/chat/completions", invokeResolveUrl());
    }

    @Test
    public void resolveUrlBaseWithoutTrailingSlash() throws Exception {
        aiProperties.setBaseUrl("https://custom.api/v1");
        assertEquals("https://custom.api/v1/chat/completions", invokeResolveUrl());
    }

    @Test
    public void resolveUrlNullProviderDefaultsDashscope() throws Exception {
        aiProperties.setBaseUrl("");
        aiProperties.setProvider(null);
        assertEquals("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", invokeResolveUrl());
    }

    private String invokeResolveUrl() throws Exception {
        Method m = OpenAiCompatibleClient.class.getDeclaredMethod("resolveUrl");
        m.setAccessible(true);
        return (String) m.invoke(client);
    }

    private String invokeExtractContent(String resp) throws Exception {
        Method m = OpenAiCompatibleClient.class.getDeclaredMethod("extractContent", String.class);
        m.setAccessible(true);
        return (String) m.invoke(client, resp);
    }

    private String invokeCoalesceText(Object content) throws Exception {
        Method m = OpenAiCompatibleClient.class.getDeclaredMethod("coalesceText", Object.class);
        m.setAccessible(true);
        return (String) m.invoke(client, content);
    }

    private String invokeTruncate(String text) throws Exception {
        Method m = OpenAiCompatibleClient.class.getDeclaredMethod("truncate", String.class);
        m.setAccessible(true);
        return (String) m.invoke(client, text);
    }
}
