package com.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * Crypto helpers. Symmetric keys are loaded from env/system properties
 * (WAREHOUSE_DES_KEY, WAREHOUSE_AES_KEY, WAREHOUSE_AES_IV) — never hard-coded.
 * AES uses GCM. DES remains only for legacy ciphertext.
 * MD5 is retained for existing password hashes (legacy schema).
 */
public class EncryptUtil {

    private static final String DES_ALGORITHM = "DES";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;

    private static String config(String envKey, String propKey) {
        String v = System.getenv(envKey);
        if (v != null && !v.isEmpty()) {
            return v;
        }
        v = System.getProperty(propKey);
        if (v != null && !v.isEmpty()) {
            return v;
        }
        return null;
    }

    private static String requireDesKey() {
        String key = config("WAREHOUSE_DES_KEY", "warehouse.des.key");
        if (key == null || key.length() < 8) {
            throw new IllegalStateException("WAREHOUSE_DES_KEY / warehouse.des.key must be set (min 8 chars)");
        }
        return key.substring(0, 8);
    }

    private static String requireAesKey() {
        String key = config("WAREHOUSE_AES_KEY", "warehouse.aes.key");
        if (key == null || key.length() < 16) {
            throw new IllegalStateException("WAREHOUSE_AES_KEY / warehouse.aes.key must be set (min 16 chars)");
        }
        return key.substring(0, 16);
    }

    private static byte[] requireAesIvBytes() {
        String iv = config("WAREHOUSE_AES_IV", "warehouse.aes.iv");
        if (iv == null || iv.length() < GCM_IV_BYTES) {
            throw new IllegalStateException("WAREHOUSE_AES_IV / warehouse.aes.iv must be set (min 12 chars)");
        }
        return iv.substring(0, GCM_IV_BYTES).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Legacy password hash (MD5). Kept for compatibility with existing DB hashes.
     */
    @SuppressWarnings("java:S4790")
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        return DigestUtil.md5Hex(text); // NOSONAR java:S4790 - legacy password hashes in DB
    }

    public static String sha256(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
            for (byte b : digest) {
                stringBuilder.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return stringBuilder.toString();
    }

    /** @deprecated Prefer AES; kept for legacy DES ciphertext. */
    @Deprecated
    public static String desEncrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            String key = requireDesKey();
            KeySpec keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM); // NOSONAR java:S5542,java:S5547 - legacy DES only
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                | IllegalStateException e) {
            return null;
        }
    }

    /** @deprecated Prefer AES; kept for legacy DES ciphertext. */
    @Deprecated
    public static String desDecrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            String key = requireDesKey();
            KeySpec keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            byte[] decodedData = Base64.getDecoder().decode(text);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM); // NOSONAR java:S5542,java:S5547 - legacy DES only
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
                | IllegalStateException | IllegalArgumentException e) {
            return null;
        }
    }

    public static String aesEncrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(requireAesKey().getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_BITS, requireAesIvBytes());
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcm);
            byte[] encryptedData = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            return null;
        }
    }

    public static String aesDecrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(requireAesKey().getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_BITS, requireAesIvBytes());
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcm);
            byte[] decodedData = Base64.getDecoder().decode(text);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
