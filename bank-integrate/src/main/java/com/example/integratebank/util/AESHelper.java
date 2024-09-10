package com.example.integratebank.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESHelper {

    // key for encrypt card
    @Value("${aes.secret.key:}")
    private String secretKey;

    @Value("${aes.salt:}")
    private String salt;

    /**
     * Encrypt data with AES
     * @param data String
     * @return String
     */
    public String encrypt(String data) {
        try {
            String subStringKey = getKey(secretKey);
            String subStringSalt = getSalt(salt);
            SecretKeySpec secretKeySpec = getSecretKeySpec(subStringKey);
            IvParameterSpec ivParameterSpec = getIvParameterSpec(subStringSalt);

            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Decrypt data with AES
     * @param encryptedData String
     * @return String
     * @throws Exception
     */
    public String decrypt(String encryptedData) throws Exception {
        String subStringKey = getKey(secretKey);
        String subStringSalt = getSalt(salt);
        SecretKeySpec secretKeySpec = getSecretKeySpec(subStringKey);
        IvParameterSpec ivParameterSpec = getIvParameterSpec(subStringSalt);
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * Generates a SecretKeySpec object for AES encryption.
     * @param key String (must be at least 32 characters).
     * @return SecretKeySpec
     */
    private SecretKeySpec getSecretKeySpec(String key) {
        String subStringKey = getKey(key);
        return new SecretKeySpec(subStringKey.getBytes(StandardCharsets.UTF_8), "AES");
    }

    /**
     * Generates an IvParameterSpec object for AES encryption using the provided salt.
     * @param salt String (must be at least 16 characters).
     * @return IvParameterSpec
     */
    private IvParameterSpec getIvParameterSpec(String salt) {
        String subStringSalt = getSalt(salt);
        return new IvParameterSpec(subStringSalt.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Initializes a Cipher object for AES encryption or decryption.
     * @param mode The mode (Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
     * @param secretKeySpec The SecretKeySpec for the cipher.
     * @param ivParameterSpec The IvParameterSpec for the cipher.
     * @return Cipher The initialized Cipher object.
     * @throws Exception If cipher initialization fails.
     */
    private Cipher initCipher(int mode, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher;
    }

    private String getKey(String key) {
        return key.substring(0, 32);
    }

    private String getSalt(String salt) {
        return salt.substring(0, 16);
    }
}
