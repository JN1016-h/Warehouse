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
 * AipPicTrans unit tests.
 */
public class AipPicTransTest {

    private File tempImage;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        tempImage = Files.createTempFile("pic-trans-", ".png").toFile();
        tempImage.deleteOnExit();
    }

    @Test
    public void testConstructor() {
        AipPicTrans client = new AipPicTrans("appId", "apiKey", "secretKey");
        assertNotNull(client);
    }

    @Test
    public void testPicTrans_noPermissionBranch() throws Exception {
        AipPicTrans client = new AipPicTrans("appId", "apiKey", "secretKey");
        setBceKey(client, true);

        JSONObject result = client.picTrans("zh", "en", tempImage);
        assertNotNull(result);
        assertTrue(result.has("error_code"));
    }

    @Test
    public void testPicTrans_apiBranchWhenNotBceKey() throws Exception {
        AipPicTrans client = new AipPicTrans("appId", "apiKey", "secretKey");
        setBceKey(client, false);
        JSONObject result = client.picTrans("zh", "en", tempImage);
        assertNotNull(result);
    }

    private static void setBceKey(BaseClient client, boolean value) throws Exception {
        Field field = BaseClient.class.getDeclaredField("isBceKey");
        field.setAccessible(true);
        ((AtomicBoolean) field.get(client)).set(value);
    }
}
