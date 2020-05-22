package com.heartsuit.string;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StringEncryptDecryptTest {
    @Autowired
    private StringEncryptDecrypt stringEncryptDecrypt;

    @Test
    void encrypt() {
        stringEncryptDecrypt.encrypt("root");
    }

    @Test
    void decrypt() {
        String decrypted = stringEncryptDecrypt.decrypt("DulBXaIv3tyJIA9xzq1u7w==");
        assertEquals("root", decrypted);
    }
}