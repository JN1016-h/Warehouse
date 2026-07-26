package com.ai.controller;

import com.ai.dto.AiChatRequest;
import com.ai.dto.AiChatResponse;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import com.ai.service.AiChatService;
import com.ai.service.ChatHistoryService;
import com.ai.service.DataScopeResolver;
import com.controller.ControllerTestSupport;
import com.enums.UserRole;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AiChatControllerTest {

    @Mock
    private AiChatService aiChatService;

    @Mock
    private ChatHistoryService chatHistoryService;

    @Mock
    private DataScopeResolver dataScopeResolver;

    @InjectMocks
    private AiChatController controller;

    private DataScopeResolver.ResolvedUser allowedUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        allowedUser = new DataScopeResolver.ResolvedUser();
        allowedUser.userId = 1L;
        allowedUser.dataScope = DataScopeResolver.SCOPE_FINANCE_OK;
        allowedUser.role = UserRole.INTERNAL_STAFF;
        when(dataScopeResolver.resolve(any())).thenReturn(allowedUser);
        when(dataScopeResolver.canUseAi(DataScopeResolver.SCOPE_FINANCE_OK)).thenReturn(true);
        when(dataScopeResolver.canAccessFinance(DataScopeResolver.SCOPE_FINANCE_OK)).thenReturn(true);
    }

    @Test
    public void testChatSuccess() {
        AiChatResponse response = new AiChatResponse();
        response.setAnswer("ok");
        when(aiChatService.chat(any(AiChatRequest.class), eq(allowedUser))).thenReturn(response);

        R result = controller.chat(new AiChatRequest(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testChatFailure() {
        when(aiChatService.chat(any(AiChatRequest.class), eq(allowedUser)))
                .thenThrow(new RuntimeException("boom"));
        R result = controller.chat(new AiChatRequest(), ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testSessions() {
        when(chatHistoryService.listSessions(1L)).thenReturn(Collections.singletonList(new AiChatSessionEntity()));
        R result = controller.sessions(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSessionsDenied() {
        allowedUser.dataScope = DataScopeResolver.SCOPE_DENIED;
        when(dataScopeResolver.canUseAi(DataScopeResolver.SCOPE_DENIED)).thenReturn(false);
        R result = controller.sessions(ControllerTestSupport.mockAdminRequest());
        assertEquals(403, result.get("code"));
    }

    @Test
    public void testMessages() {
        when(chatHistoryService.listMessages(10L, 1L))
                .thenReturn(Collections.singletonList(new AiChatMessageEntity()));
        R result = controller.messages(10L, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPreviewSuccess() {
        when(aiChatService.preview(any(AiChatRequest.class), eq(allowedUser)))
                .thenReturn(Collections.singletonMap("total", 1));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("question", "库存");
        R result = controller.preview(params, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPreviewIllegalState() {
        when(aiChatService.preview(any(AiChatRequest.class), eq(allowedUser)))
                .thenThrow(new IllegalStateException("forbidden"));
        R result = controller.preview(new HashMap<String, Object>(), ControllerTestSupport.mockAdminRequest());
        assertEquals(403, result.get("code"));
    }

    @Test
    public void testScope() {
        R result = controller.scope(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertEquals(DataScopeResolver.SCOPE_FINANCE_OK, result.get("dataScope"));
    }

    @Test
    public void testMessagesDenied() {
        allowedUser.dataScope = DataScopeResolver.SCOPE_DENIED;
        when(dataScopeResolver.canUseAi(DataScopeResolver.SCOPE_DENIED)).thenReturn(false);
        R result = controller.messages(10L, ControllerTestSupport.mockAdminRequest());
        assertEquals(403, result.get("code"));
    }

    @Test
    public void testPreviewGenericError() {
        when(aiChatService.preview(any(AiChatRequest.class), eq(allowedUser)))
                .thenThrow(new RuntimeException("preview failed"));
        R result = controller.preview(new HashMap<String, Object>(), ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testScopeNullRole() {
        allowedUser.role = null;
        R result = controller.scope(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNull(result.get("role"));
    }

    @Test
    public void testPreviewWithAllParams() {
        when(aiChatService.preview(any(AiChatRequest.class), eq(allowedUser)))
                .thenReturn(Collections.singletonMap("total", 1));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("question", "库存");
        params.put("timeRange", "QUARTER");
        params.put("startDate", "2025-01-01");
        params.put("endDate", "2025-03-31");
        R result = controller.preview(params, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }
}
