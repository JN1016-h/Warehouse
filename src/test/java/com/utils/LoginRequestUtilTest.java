package com.utils;

import com.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginRequestUtil unit tests.
 */
public class LoginRequestUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMerge_queryOnly() {
        String[] merged = LoginRequestUtil.merge(" user ", " pass ", " cap ", null);
        assertArrayEquals(new String[] { "user", "pass", "cap" }, merged);
    }

    @Test
    public void testMerge_bodyOverridesNonBlankUsername() {
        LoginRequest body = new LoginRequest();
        body.setUsername("jsonUser");
        body.setPassword("jsonPass");
        body.setCaptcha("jsonCap");

        String[] merged = LoginRequestUtil.merge("queryUser", "queryPass", "queryCap", body);
        assertEquals("jsonUser", merged[0]);
        assertEquals("jsonPass", merged[1]);
        assertEquals("jsonCap", merged[2]);
    }

    @Test
    public void testMerge_blankBodyUsernameKeepsQuery() {
        LoginRequest body = new LoginRequest();
        body.setUsername("   ");
        body.setPassword("onlyPass");

        String[] merged = LoginRequestUtil.merge("queryUser", "queryPass", null, body);
        assertEquals("queryUser", merged[0]);
        assertEquals("onlyPass", merged[1]);
        assertNull(merged[2]);
    }

    @Test
    public void testMerge_nullFieldsTrimmed() {
        String[] merged = LoginRequestUtil.merge(null, null, null, null);
        assertNull(merged[0]);
        assertNull(merged[1]);
        assertNull(merged[2]);
    }
}
