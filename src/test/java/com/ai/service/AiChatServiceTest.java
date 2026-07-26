package com.ai.service;

import com.ai.cache.AiResultCache;
import com.ai.config.AiProperties;
import com.ai.dto.AiChatRequest;
import com.ai.dto.AiChatResponse;
import com.ai.dto.AiIntent;
import com.ai.dto.TimeRange;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AiChatServiceTest {

    @Mock
    private IntentRouter intentRouter;
    @Mock
    private TimeRangeResolver timeRangeResolver;
    @Mock
    private AnalyticsFacade analyticsFacade;
    @Mock
    private LlmGateway llmGateway;
    @Mock
    private AiResultCache aiResultCache;
    @Mock
    private ChatHistoryService chatHistoryService;

    @InjectMocks
    private AiChatService aiChatService;

    private DataScopeResolver.ResolvedUser financeUser;
    private TimeRange range;
    private Map<String, Object> data;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        financeUser = new DataScopeResolver.ResolvedUser();
        financeUser.userId = 1L;
        financeUser.tableName = "yonghu";
        financeUser.dataScope = DataScopeResolver.SCOPE_FINANCE_OK;

        range = new TimeRange(new java.util.Date(), new java.util.Date(), "近月");
        data = new HashMap<String, Object>();
        data.put("hasData", true);

        when(timeRangeResolver.resolve(any(), any(), any())).thenReturn(range);
        when(analyticsFacade.assemble(any(), any(), anyBoolean())).thenReturn(data);

        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setId(100L);
        when(chatHistoryService.getOrCreateSession(any(), any(), any(), any(), any())).thenReturn(session);

        AiChatMessageEntity assistant = new AiChatMessageEntity();
        assistant.setId(200L);
        when(chatHistoryService.saveMessage(anyLong(), anyString(), anyString(), any(), anyBoolean(), anyString()))
                .thenReturn(assistant);
    }

    @Test
    public void chatDeniedUser() {
        DataScopeResolver.ResolvedUser denied = new DataScopeResolver.ResolvedUser();
        denied.dataScope = DataScopeResolver.SCOPE_DENIED;

        AiChatResponse resp = aiChatService.chat(request("库存"), denied);

        assertTrue(resp.isDenied());
        assertTrue(resp.isDegraded());
        verify(analyticsFacade, never()).assemble(any(), any(), anyBoolean());
    }

    @Test
    public void chatEmptyQuestion() {
        AiChatResponse resp = aiChatService.chat(request(""), financeUser);

        assertEquals("请输入要查询的问题。", resp.getAnswer());
        verify(intentRouter, never()).route(anyString());
    }

    @Test
    public void chatFinanceDeniedForWarehouse() {
        DataScopeResolver.ResolvedUser warehouse = new DataScopeResolver.ResolvedUser();
        warehouse.userId = 2L;
        warehouse.tableName = "yonghu";
        warehouse.dataScope = DataScopeResolver.SCOPE_WAREHOUSE;

        when(intentRouter.route(anyString())).thenReturn(AiIntent.FINANCE);

        AiChatResponse resp = aiChatService.chat(request("应收应付"), warehouse);

        assertTrue(resp.isDenied());
        assertEquals("FINANCE", resp.getIntent());
        verify(analyticsFacade, never()).assemble(any(), any(), anyBoolean());
    }

    @Test
    public void chatUsesCache() {
        when(intentRouter.route(anyString())).thenReturn(AiIntent.INVENTORY);
        when(aiResultCache.get(anyString())).thenReturn("缓存回答");

        AiChatResponse resp = aiChatService.chat(request("库存概况"), financeUser);

        assertTrue(resp.isCached());
        assertEquals("缓存回答", resp.getAnswer());
        assertFalse(resp.isDegraded());
        verify(llmGateway, never()).generate(anyString(), any(), anyString(), any());
    }

    @Test
    public void chatCallsLlmAndCachesSuccess() {
        when(intentRouter.route(anyString())).thenReturn(AiIntent.SELL_THROUGH);
        when(aiResultCache.get(anyString())).thenReturn(null);

        LlmGateway.Result llmResult = new LlmGateway.Result();
        llmResult.answer = "## 动销结论";
        llmResult.degraded = false;
        when(llmGateway.generate(anyString(), any(), anyString(), any())).thenReturn(llmResult);

        AiChatResponse resp = aiChatService.chat(request("动销分析"), financeUser);

        assertEquals("## 动销结论", resp.getAnswer());
        assertFalse(resp.isDegraded());
        verify(aiResultCache).put(anyString(), eq("## 动销结论"));
    }

    @Test
    public void chatDoesNotCacheDegraded() {
        when(intentRouter.route(anyString())).thenReturn(AiIntent.RISK);
        when(aiResultCache.get(anyString())).thenReturn(null);

        LlmGateway.Result llmResult = new LlmGateway.Result();
        llmResult.answer = "降级";
        llmResult.degraded = true;
        when(llmGateway.generate(anyString(), any(), anyString(), any())).thenReturn(llmResult);

        aiChatService.chat(request("风险"), financeUser);

        verify(aiResultCache, never()).put(anyString(), anyString());
    }

    @Test
    public void previewSuccess() {
        when(intentRouter.route(anyString())).thenReturn(AiIntent.TURNOVER);

        Map<String, Object> preview = aiChatService.preview(request("周转"), financeUser);

        assertSame(data, preview);
    }

    @Test
    public void previewDeniedThrows() {
        DataScopeResolver.ResolvedUser denied = new DataScopeResolver.ResolvedUser();
        denied.dataScope = DataScopeResolver.SCOPE_DENIED;

        assertThrows(IllegalStateException.class, () -> aiChatService.preview(request("x"), denied));
    }

    @Test
    public void previewFinanceDeniedThrows() {
        DataScopeResolver.ResolvedUser warehouse = new DataScopeResolver.ResolvedUser();
        warehouse.dataScope = DataScopeResolver.SCOPE_WAREHOUSE;
        when(intentRouter.route(anyString())).thenReturn(AiIntent.FINANCE);

        assertThrows(IllegalStateException.class, () -> aiChatService.preview(request("应收"), warehouse));
    }

    @Test
    public void exportReportMarkdown() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setIntent("INVENTORY");
        msg.setDegraded(0);
        msg.setContent("结论内容");
        msg.setDataSnapshot("{\"hasData\":true}");
        when(chatHistoryService.getMessage(1L, 1L)).thenReturn(msg);

        String md = aiChatService.exportReport(1L, 1L, "md");

        assertTrue(md.contains("# AI 分析报告"));
        assertTrue(md.contains("结论内容"));
    }

    @Test
    public void exportReportCsv() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setIntent("FINANCE");
        msg.setDegraded(1);
        msg.setContent("含\"引号\"");
        msg.setDataSnapshot("{}");
        when(chatHistoryService.getMessage(2L, 1L)).thenReturn(msg);

        String csv = aiChatService.exportReport(2L, 1L, "csv");

        assertTrue(csv.contains("role,intent,degraded,content"));
        assertTrue(csv.contains("含\"\"引号\"\""));
    }

    @Test
    public void exportReportNotFound() {
        when(chatHistoryService.getMessage(99L, 1L)).thenReturn(null);
        assertNull(aiChatService.exportReport(99L, 1L, "md"));
    }

    @Test
    public void chatNullUserDenied() {
        AiChatResponse resp = aiChatService.chat(request("库存"), null);
        assertTrue(resp.isDenied());
    }

    @Test
    public void chatFinanceDeniedSavesHistory() {
        DataScopeResolver.ResolvedUser warehouse = new DataScopeResolver.ResolvedUser();
        warehouse.userId = 2L;
        warehouse.tableName = "yonghu";
        warehouse.dataScope = DataScopeResolver.SCOPE_WAREHOUSE;
        when(intentRouter.route(anyString())).thenReturn(AiIntent.FINANCE);

        AiChatResponse resp = aiChatService.chat(request("应收"), warehouse);

        assertTrue(resp.isDenied());
        verify(chatHistoryService, times(2)).saveMessage(anyLong(), anyString(), anyString(), any(), anyBoolean(), anyString());
    }

    @Test
    public void chatNullQuestionUsesEmpty() {
        AiChatRequest req = request("");
        req.setQuestion(null);
        AiChatResponse resp = aiChatService.chat(req, financeUser);
        assertEquals("请输入要查询的问题。", resp.getAnswer());
    }

    @Test
    public void exportReportMarkdownNullFields() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setIntent("GENERAL");
        msg.setDegraded(0);
        msg.setContent(null);
        msg.setDataSnapshot(null);
        when(chatHistoryService.getMessage(3L, 1L)).thenReturn(msg);

        String md = aiChatService.exportReport(3L, 1L, "md");
        assertTrue(md.contains("# AI 分析报告"));
        assertTrue(md.contains("false"));
    }

    @Test
    public void previewNullUserThrows() {
        assertThrows(IllegalStateException.class, () -> aiChatService.preview(request("x"), null));
    }

    private AiChatRequest request(String question) {
        AiChatRequest req = new AiChatRequest();
        req.setQuestion(question);
        req.setStyle("SIMPLE");
        req.setTimeRange("MONTH");
        return req;
    }
}
