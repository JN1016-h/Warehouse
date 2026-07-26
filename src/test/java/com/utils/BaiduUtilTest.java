package com.utils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BaiduUtil unit tests (mocked HTTP/OCR; no live Baidu API calls).
 */
public class BaiduUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        resetOcrClient();
    }

    @AfterEach
    public void tearDown() {
        resetOcrClient();
    }

    @Test
    public void testGetCityByLonLat_success() {
        String json = "{"
                + "\"result\":{\"addressComponent\":{"
                + "\"province\":\"北京市\",\"city\":\"北京市\","
                + "\"district\":\"海淀区\",\"street\":\"中关村\"}}}";

        try (MockedStatic<HttpClientUtils> http = mockStatic(HttpClientUtils.class)) {
            http.when(() -> HttpClientUtils.doGet(anyString())).thenReturn(json);
            Map<String, String> area = BaiduUtil.getCityByLonLat("key", "116.3", "39.9");
            assertNotNull(area);
            assertEquals("北京市", area.get("province"));
            assertEquals("海淀区", area.get("district"));
        }
    }

    @Test
    public void testGetCityByLonLat_malformedJsonReturnsNull() {
        try (MockedStatic<HttpClientUtils> http = mockStatic(HttpClientUtils.class)) {
            http.when(() -> HttpClientUtils.doGet(anyString())).thenReturn("not-json");
            assertNull(BaiduUtil.getCityByLonLat("key", "1", "2"));
        }
    }

    @Test
    public void testGetCityByLonLat_httpFailureReturnsNull() {
        try (MockedStatic<HttpClientUtils> http = mockStatic(HttpClientUtils.class)) {
            http.when(() -> HttpClientUtils.doGet(anyString())).thenReturn(null);
            assertNull(BaiduUtil.getCityByLonLat("key", "1", "2"));
        }
    }

    @Test
    public void testGeneralString_successWithNewline() throws Exception {
        JSONObject json = buildWordsJson("Line1", "Line2");
        AipOcr ocr = mock(AipOcr.class);
        when(ocr.basicAccurateGeneral(anyString(), any(HashMap.class))).thenReturn(json);
        setOcrClient(ocr);

        assertEquals("Line1\nLine2\n", BaiduUtil.generalString("img.png", true));
    }

    @Test
    public void testGeneralString_successWithoutNewline() throws Exception {
        JSONObject json = buildWordsJson("A", "B");
        AipOcr ocr = mock(AipOcr.class);
        when(ocr.basicAccurateGeneral(anyString(), any(HashMap.class))).thenReturn(json);
        setOcrClient(ocr);

        assertEquals("AB", BaiduUtil.generalString("img.png", false));
    }

    @Test
    public void testGeneralString_ocrThrowsReturnsNull() throws Exception {
        AipOcr ocr = mock(AipOcr.class);
        when(ocr.basicAccurateGeneral(anyString(), any(HashMap.class))).thenThrow(new RuntimeException("ocr down"));
        setOcrClient(ocr);

        assertNull(BaiduUtil.generalString("img.png", false));
    }

    @Test
    public void testGeneralString_invalidPathReturnsErrorJson() {
        String result = BaiduUtil.generalString("non-existent-" + System.nanoTime(), false);
        assertNotNull(result);
        assertTrue(result.contains("error"));
    }

    @Test
    public void testMergeString_branchesViaReflection() throws Exception {
        Method merge = BaiduUtil.class.getDeclaredMethod("mergeString", JSONObject.class, boolean.class);
        merge.setAccessible(true);

        assertEquals("", merge.invoke(null, new Object[] { null, false }));

        JSONObject emptyWords = new JSONObject();
        emptyWords.put("words_result_num", 0);
        emptyWords.put("words_result", new JSONArray());
        assertNull(merge.invoke(null, emptyWords, false));

        JSONObject withWords = buildWordsJson("Hello", "World");
        assertEquals("HelloWorld", merge.invoke(null, withWords, false));
        assertEquals("Hello\nWorld\n", merge.invoke(null, withWords, true));

        JSONObject fallback = new JSONObject();
        fallback.put("error_code", 1);
        assertEquals(fallback.toString(), merge.invoke(null, fallback, false));
    }

    @Test
    public void testDetectWrappers_returnJSONObject() {
        String badPath = "missing-" + System.nanoTime() + ".jpg";
        assertNotNull(BaiduUtil.animalDetect(badPath));
        assertNotNull(BaiduUtil.dishDetect(badPath));
        assertNotNull(BaiduUtil.plantDetect(badPath));
        assertNotNull(BaiduUtil.advancedGeneral(badPath));
        assertNotNull(BaiduUtil.carDetect(badPath));
        assertNotNull(BaiduUtil.bodyNum(badPath));
    }

    @Test
    public void testAsrAndTranslateWrappers_returnJSONObject() {
        assertNotNull(BaiduUtil.asr("missing-" + System.nanoTime() + ".wav"));
        assertNotNull(BaiduUtil.textTrans("zh", "en", "hello"));
    }

    @Test
    public void testPicTrans_returnsJSONObject() throws Exception {
        File temp = File.createTempFile("baidu-pic-", ".png");
        temp.deleteOnExit();
        assertNotNull(BaiduUtil.picTrans("zh", "en", temp));
    }

    @Test
    public void testGetAuth_unreachableReturnsNull() {
        assertNull(BaiduUtil.getAuth("invalid-ak", "invalid-sk"));
    }

    @Test
    public void testConstantsPresent() {
        assertNotNull(BaiduUtil.APP_ID);
        assertNotNull(BaiduUtil.API_KEY);
        assertNotNull(BaiduUtil.SECRET_KEY);
    }

    private static JSONObject buildWordsJson(String... words) throws Exception {
        JSONObject root = new JSONObject();
        JSONArray arr = new JSONArray();
        for (String word : words) {
            JSONObject item = new JSONObject();
            item.put("words", word);
            arr.put(item);
        }
        root.put("words_result_num", words.length);
        root.put("words_result", arr);
        return root;
    }

    private static void setOcrClient(AipOcr client) throws Exception {
        Field field = BaiduUtil.class.getDeclaredField("ocrClient");
        field.setAccessible(true);
        field.set(null, client);
    }

    private static void resetOcrClient() {
        try {
            setOcrClient(null);
        } catch (Exception ignored) {
        }
    }
}
