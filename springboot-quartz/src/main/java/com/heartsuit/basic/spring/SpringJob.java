package com.heartsuit.basic.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author Heartsuit
 * @Date 2021-10-22
 */
@Configuration
//@EnableScheduling
@Slf4j
public class SpringJob {
    @Scheduled(cron = "0/5 * * * * ?")
    private void cron() {
        log.info("Job Executing: " + new Date().getTime());
        log.info("Spring Job Executing: " + LocalDateTime.now());
    }

    /*
     * 参考：江南一点雨 微信公众号文章: Spring Boot 中实现定时任务的两种方式!
     * 首先使用 @Scheduled 注解开启一个定时任务。
     * fixedRate 表示任务执行之间的时间间隔，具体是指两次任务的开始时间间隔，即第二次任务开始时，第一次任务可能还没结束。
     * fixedDelay 表示任务执行之间的时间间隔，具体是指本次任务结束到下次任务开始之间的时间间隔。
     * initialDelay 表示首次任务启动的延迟时间。
     * 所有时间的单位都是毫秒。
     * */
    @Scheduled(fixedRate = 3000)
    private void fixedRate() {
        log.info("Fixed rate: " + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 3000)
    private void fixedDelay() {
        log.info("Fixed delay: " + LocalDateTime.now());
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 3000)
    private void initialDelay() {
        log.info("Initial delay: " + LocalDateTime.now());
    }
}
