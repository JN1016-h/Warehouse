package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * R response wrapper unit tests.
 */
public class RTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOkDefault() {
        R r = R.ok();
        assertEquals(0, r.get("code"));
    }

    @Test
    public void testOkWithMsg() {
        R r = R.ok("success");
        assertEquals(0, r.get("code"));
        assertEquals("success", r.get("msg"));
    }

    @Test
    public void testOkWithMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 10);
        R r = R.ok(data);
        assertEquals(0, r.get("code"));
        assertEquals(10, r.get("total"));
    }

    @Test
    public void testErrorVariants() {
        R defaultError = R.error();
        assertEquals(500, defaultError.get("code"));
        assertEquals("未知异常，请联系管理员", defaultError.get("msg"));

        R msgError = R.error("bad request");
        assertEquals(500, msgError.get("code"));
        assertEquals("bad request", msgError.get("msg"));

        R codeError = R.error(403, "forbidden");
        assertEquals(403, codeError.get("code"));
        assertEquals("forbidden", codeError.get("msg"));
    }

    @Test
    public void testPutChaining() {
        R r = new R().put("key", "value");
        assertEquals("value", r.get("key"));
        assertEquals(0, r.get("code"));
    }
}
