package com.heartsuit.string;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Heartsuit
 * @Date 2020-05-22
 */
@Component
public class StringEncryptDecrypt {

    @Autowired
    StringEncryptor encryptor;

    public String encrypt(String plainText) {
        // Encrypt
        String encrypted = encryptor.encrypt(plainText);
        System.out.println("Encrypted: " + encrypted);
        return encrypted;
    }

    public String decrypt(String encrypted) {
        // Decrypt
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
}
