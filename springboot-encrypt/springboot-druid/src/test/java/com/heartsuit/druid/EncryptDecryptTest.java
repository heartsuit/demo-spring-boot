package com.heartsuit.druid;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EncryptDecryptTest {
    @Autowired
    private EncryptDecrypt encryptDecrypt;

    @Test
    void encrypt() throws Exception {
        encryptDecrypt.encrypt();
    }

    @Test
    void decrypt() throws Exception {
        encryptDecrypt.decrypt();
    }
}