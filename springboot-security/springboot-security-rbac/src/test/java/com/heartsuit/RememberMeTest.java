package com.heartsuit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Base64;

/**
 * @Author Heartsuit
 * @Date 2020-12-21
 */
@Slf4j
public class RememberMeTest {
    @Test
    public void decodeBase64() {
        byte[] decoded = Base64.getDecoder().decode("ZGV2OjE2MDk3NDQ3NDcyOTg6OTUyZTFkYTNiMGJjMTFkNGE2YmNiNThlMmEzMmNjODQ");
        log.info("Decoded: {}", new String(decoded));
    }
}
