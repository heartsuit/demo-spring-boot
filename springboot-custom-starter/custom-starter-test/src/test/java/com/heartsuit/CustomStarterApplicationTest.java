package com.heartsuit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author:  Heartsuit
 * Date:  2021/6/29 8:32
 */
@SpringBootTest
@Slf4j
class CustomStarterApplicationTest {
    @Autowired
    RedissonClient redisson;

    @Test
    void test() throws IOException {
//        Config config = Config.fromYAML(new ClassPathResource("application.yml").getInputStream());
//        RedissonClient redisson = Redisson.create(config);

        RKeys keys = redisson.getKeys();
        log.info("Keys count: {}", keys.count());
        assertTrue(keys.count() > 0);

        log.info("Keys forEach: ");
//        keys.getKeysStream().forEach(key -> log.info(key));
        keys.getKeysStream().forEach(log::info);

        log.info("Keys iterator: ");
        keys.getKeys().forEach(log::info);
    }
}