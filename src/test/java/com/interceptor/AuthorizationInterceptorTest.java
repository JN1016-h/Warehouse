package com.interceptor;

import com.annotation.IgnoreAuth;
import com.entity.TokenEntity;
import com.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthorizationInterceptorTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthorizationInterceptor interceptor;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    public void preHandleOptionsReturnsFalse() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.OPTIONS.name());

        boolean result = interceptor.preHandle(request, response, handlerMethod("secured"));

        assertFalse(result);
        verify(response).setStatus(HttpStatus.OK.value());
    }

    @Test
    public void preHandleNonHandlerMethodPasses() throws Exception {
        when(request.getMethod()).thenReturn("GET");

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void preHandleIgnoreAuthPassesWithoutToken() throws Exception {
        when(request.getMethod()).thenReturn("GET");

        assertTrue(interceptor.preHandle(request, response, handlerMethod("open")));
        verify(tokenService, never()).getTokenEntity(anyString());
    }

    @Test
    public void preHandleValidTokenInHeader() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn("valid-token");

        TokenEntity tokenEntity = validToken();
        when(tokenService.getTokenEntity("valid-token")).thenReturn(tokenEntity);

        assertTrue(interceptor.preHandle(request, response, handlerMethod("secured")));

        verify(session).setAttribute("userId", tokenEntity.getUserid());
        verify(session).setAttribute("role", tokenEntity.getRole());
        verify(session).setAttribute("tableName", tokenEntity.getTablename());
        verify(session).setAttribute("username", tokenEntity.getUsername());
    }

    @Test
    public void preHandleValidTokenInQueryParam() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn("");
        when(request.getParameter("token")).thenReturn("query-token");

        TokenEntity tokenEntity = validToken();
        when(tokenService.getTokenEntity("query-token")).thenReturn(tokenEntity);

        assertTrue(interceptor.preHandle(request, response, handlerMethod("secured")));
    }

    @Test
    public void preHandleValidBearerToken() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer bearer-token");

        TokenEntity tokenEntity = validToken();
        when(tokenService.getTokenEntity("bearer-token")).thenReturn(tokenEntity);

        assertTrue(interceptor.preHandle(request, response, handlerMethod("secured")));
    }

    @Test
    public void preHandleInvalidTokenReturns401() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn("bad");
        when(tokenService.getTokenEntity("bad")).thenReturn(null);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        assertFalse(interceptor.preHandle(request, response, handlerMethod("secured")));
        assertTrue(sw.toString().contains("401"));
        assertTrue(sw.toString().contains("请先登录"));
    }

    @Test
    public void preHandleSetsCorsHeadersWhenOriginPresent() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://localhost:8080");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn("t");
        when(tokenService.getTokenEntity("t")).thenReturn(validToken());

        interceptor.preHandle(request, response, handlerMethod("secured"));

        verify(response).setHeader(eq("Access-Control-Allow-Origin"), eq("http://localhost:8080"));
        verify(response).setHeader(eq("Cache-Control"), contains("no-store"));
    }

    @Test
    public void preHandleRejectsUntrustedOrigin() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://evil.example");
        when(request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY)).thenReturn("t");
        when(tokenService.getTokenEntity("t")).thenReturn(validToken());

        interceptor.preHandle(request, response, handlerMethod("secured"));

        verify(response, never()).setHeader(eq("Access-Control-Allow-Origin"), eq("http://evil.example"));
    }

    private HandlerMethod handlerMethod(String methodName) throws NoSuchMethodException {
        return new HandlerMethod(new TestController(), TestController.class.getDeclaredMethod(methodName));
    }

    private TokenEntity validToken() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        TokenEntity token = new TokenEntity(1L, "user1", "yonghu", "管理员", "token", cal.getTime());
        return token;
    }

    static class TestController {
        @IgnoreAuth
        public void open() {
        }

        public void secured() {
        }
    }
}
