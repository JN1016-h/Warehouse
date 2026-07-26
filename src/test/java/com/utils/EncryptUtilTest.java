package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EncryptUtil unit tests.
 */
public class EncryptUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMd5_nullAndNormal() {
        assertNull(EncryptUtil.md5(null));
        assertEquals("5d41402abc4b2a76b9719d911017c592", EncryptUtil.md5("hello"));
        assertEquals("098f6bcd4621d373cade4e832627b4f6", EncryptUtil.md5("test"));
    }

    @Test
    public void testSha256_nullAndNormal() {
        assertNull(EncryptUtil.sha256(null));
        String hash = EncryptUtil.sha256("hello");
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertEquals(EncryptUtil.sha256("hello"), hash);
    }

    @Test
    public void testDesEncryptDecrypt_nullAndRoundTrip() {
        assertNull(EncryptUtil.desEncrypt(null));
        assertNull(EncryptUtil.desDecrypt(null));

        String plain = "warehouse-secret";
        String encrypted = EncryptUtil.desEncrypt(plain);
        assertNotNull(encrypted);
        assertEquals(plain, EncryptUtil.desDecrypt(encrypted));
    }

    @Test
    public void testAesEncryptDecrypt_nullAndRoundTrip() {
        assertNull(EncryptUtil.aesEncrypt(null));
        assertNull(EncryptUtil.aesDecrypt(null));

        String plain = "aes-payload-中文";
        String encrypted = EncryptUtil.aesEncrypt(plain);
        assertNotNull(encrypted);
        assertEquals(plain, EncryptUtil.aesDecrypt(encrypted));
    }

    @Test
    public void testDesDecrypt_invalidBase64Throws() {
        assertThrows(IllegalArgumentException.class, () -> EncryptUtil.desDecrypt("not-valid-base64!!!"));
    }

    @Test
    public void testAesDecrypt_invalidBase64Throws() {
        assertThrows(IllegalArgumentException.class, () -> EncryptUtil.aesDecrypt("not-valid-base64!!!"));
    }

    @Test
    public void testMd5_emptyString() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", EncryptUtil.md5(""));
    }

    @Test
    public void testSha256_emptyString() {
        String hash = EncryptUtil.sha256("");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    public void testDesEncryptDecrypt_emptyString() {
        String encrypted = EncryptUtil.desEncrypt("");
        assertNotNull(encrypted);
        assertEquals("", EncryptUtil.desDecrypt(encrypted));
    }

    @Test
    public void testAesEncryptDecrypt_emptyString() {
        String encrypted = EncryptUtil.aesEncrypt("");
        assertNotNull(encrypted);
        assertEquals("", EncryptUtil.aesDecrypt(encrypted));
    }

    @Test
    public void testDesDecrypt_corruptedCiphertextReturnsNull() {
        String encrypted = EncryptUtil.desEncrypt("valid");
        assertNotNull(encrypted);
        String tampered = encrypted.substring(0, encrypted.length() - 2) + "XX";
        assertNull(EncryptUtil.desDecrypt(tampered));
    }

    @Test
    public void testAesDecrypt_corruptedCiphertextReturnsNull() {
        String encrypted = EncryptUtil.aesEncrypt("valid");
        assertNotNull(encrypted);
        String tampered = encrypted.substring(0, encrypted.length() - 2) + "XX";
        assertNull(EncryptUtil.aesDecrypt(tampered));
    }

    @Test
    public void testDesDecrypt_validBase64InvalidCipherReturnsNull() {
        assertNull(EncryptUtil.desDecrypt("YWJjZGVm"));
    }

    @Test
    public void testAesDecrypt_validBase64InvalidCipherReturnsNull() {
        assertNull(EncryptUtil.aesDecrypt("YWJjZGVm"));
    }

    @Test
    public void testDesEncryptDecrypt_specialChars() {
        String plain = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        assertEquals(plain, EncryptUtil.desDecrypt(EncryptUtil.desEncrypt(plain)));
    }

    @Test
    public void testAesEncryptDecrypt_longText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sb.append("长文本测试");
        }
        String plain = sb.toString();
        assertEquals(plain, EncryptUtil.aesDecrypt(EncryptUtil.aesEncrypt(plain)));
    }
}
