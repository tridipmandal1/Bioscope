package com.bioscope.backend.v01.utils;

import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Value("${jasypt.secret}")
    private String secret;

    public String encrypt(String plainText) {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(secret);
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(secret);
        return encryptor.decrypt(encryptedText);
    }
}
