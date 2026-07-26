package com.utils;

import com.baidu.aip.client.BaseClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AipTranslate unit tests.
 */
public class AipTranslateTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConstructor() {
        AipTranslate client = new AipTranslate("appId", "apiKey", "secretKey");
        assertNotNull(client);
    }

    @Test
    public void testTextTrans_noPermissionBranch() throws Exception {
        AipTranslate client = new AipTranslate("appId", "apiKey", "secretKey");
        setBceKey(client, true);

        JSONObject result = client.textTrans("zh", "en", "hello");
        assertNotNull(result);
        assertTrue(result.has("error_code"));
    }

    @Test
    public void testTextTrans_apiBranchWhenNotBceKey() throws Exception {
        AipTranslate client = new AipTranslate("appId", "apiKey", "secretKey");
        setBceKey(client, false);
        JSONObject result = client.textTrans("zh", "en", "hello");
        assertNotNull(result);
    }

    private static void setBceKey(BaseClient client, boolean value) throws Exception {
        Field field = BaseClient.class.getDeclaredField("isBceKey");
        field.setAccessible(true);
        ((AtomicBoolean) field.get(client)).set(value);
    }
}
