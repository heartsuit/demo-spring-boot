package com.heartsuit.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2021-11-12
 */
@Slf4j
public class QuartzCronJob extends QuartzJobBean {
    public static final String API_URL = "http://www.baidu.com";

    @Override
    protected void executeInternal(JobExecutionContext context) {
        doSomething();
    }

    private void doSomething() {
        log.info("Quartz Cron Job Executing: {}", LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        try {
            String result = restTemplate.getForObject(API_URL, String.class);
            log.info(result);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
