package com.heartsuit.springbootmybatis.oa.config;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author Heartsuit
 * @Date 2021-08-11
 */
@SpringBootTest
@Slf4j
public class SnowflakeConfigTest {

    @Autowired
    private Snowflake snowflake;

    @Test
    void snowflake() {
        long id = snowflake.nextId();
        log.info("New id: {}", id);
    }
}