package com.controller;

import com.utils.PageUtils;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Shared helpers for controller Mockito tests.
 */
public final class ControllerTestSupport {

    private ControllerTestSupport() {
    }

    public static PageUtils emptyPage() {
        return new PageUtils(Collections.emptyList(), 0, 10, 1);
    }

    public static Map<String, Object> pageParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        return params;
    }

    public static HttpServletRequest mockRequestWithSession(String tableName, String username, Long userId, String xingming) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("tableName")).thenReturn(tableName);
        when(session.getAttribute("username")).thenReturn(username);
        if (userId != null) {
            when(session.getAttribute("userId")).thenReturn(userId);
        }
        if (xingming != null) {
            when(session.getAttribute("xingming")).thenReturn(xingming);
        }
        return request;
    }

    public static HttpServletRequest mockAdminRequest() {
        return mockRequestWithSession("users", "admin", 1L, null);
    }

    /** Write a JSON cache file used by controller stat endpoints (cwd-relative). */
    public static Path writeJsonCache(String filename, String json) throws Exception {
        Path path = Paths.get(filename);
        Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        return path;
    }

    public static void deleteJsonCache(String filename) throws Exception {
        Files.deleteIfExists(Paths.get(filename));
    }
}
