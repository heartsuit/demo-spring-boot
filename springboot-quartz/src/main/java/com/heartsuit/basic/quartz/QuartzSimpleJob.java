package com.heartsuit.basic.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2021-11-12
 */
@Slf4j
@Component
public class QuartzSimpleJob {
    public void doSomething() {
        log.info("Quartz Simple Job Executing: {}", LocalDateTime.now());
    }
}
