package com.heartsuit.basic.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2021-11-12
 */
@Slf4j
public class QuartzCronJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        doSomething();
    }

    private void doSomething() {
        log.info("Quartz Cron Job Executing: {}", LocalDateTime.now());
    }
}
