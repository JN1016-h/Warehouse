package com.ai.controller;

import com.ai.service.AiChatService;
import com.ai.service.DataScopeResolver;
import com.controller.ControllerTestSupport;
import com.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AiReportControllerTest {

    @Mock
    private AiChatService aiChatService;

    @Mock
    private DataScopeResolver dataScopeResolver;

    @InjectMocks
    private AiReportController controller;

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
    }

    @Test
    public void testExportMarkdown() throws Exception {
        when(aiChatService.exportReport(eq(10L), eq(1L), eq("md"))).thenReturn("# report");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("messageId", 10);
        body.put("format", "md");

        controller.export(body, ControllerTestSupport.mockAdminRequest(), response);

        assertTrue(response.getContentAsString().contains("# report"));
    }

    @Test
    public void testExportCsv() throws Exception {
        when(aiChatService.exportReport(eq(10L), eq(1L), eq("csv"))).thenReturn("a,b");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("messageId", 10);
        body.put("format", "csv");

        controller.export(body, ControllerTestSupport.mockAdminRequest(), response);

        assertTrue(response.getContentAsString().contains("a,b"));
    }

    @Test
    public void testExportDenied() throws Exception {
        allowedUser.dataScope = DataScopeResolver.SCOPE_DENIED;
        when(dataScopeResolver.canUseAi(DataScopeResolver.SCOPE_DENIED)).thenReturn(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.export(new HashMap<String, Object>(), ControllerTestSupport.mockAdminRequest(), response);
        assertTrue(response.getContentAsString().contains("\"code\":403"));
    }

    @Test
    public void testExportMissingMessageId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.export(new HashMap<String, Object>(), ControllerTestSupport.mockAdminRequest(), response);
        assertTrue(response.getContentAsString().contains("\"code\":400"));
    }

    @Test
    public void testExportNotFound() throws Exception {
        when(aiChatService.exportReport(eq(99L), eq(1L), eq("md"))).thenReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("messageId", 99);
        controller.export(body, ControllerTestSupport.mockAdminRequest(), response);
        assertTrue(response.getContentAsString().contains("\"code\":404"));
    }
}
