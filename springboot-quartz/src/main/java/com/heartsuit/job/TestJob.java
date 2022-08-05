package com.heartsuit.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-11-09
 */
@Slf4j
public class TestJob extends QuartzJobBean {
    public static final String SENTENCES_API_URL = "http://poetry.apiopen.top/sentences";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("TestJob...");
        ResponseSentence response = callAPI();
        Map<String, String> reslult = response.getResult();
        log.info("Job Result: 诗句：{} 作者：{}", reslult.get("name"), reslult.get("from"));
    }

    private ResponseSentence callAPI() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
//        ResponseEntity<String> result = restTemplate.getForEntity(SENTENCES_API_URL, String.class);
        ResponseSentence result = restTemplate.getForObject(SENTENCES_API_URL, ResponseSentence.class);
//        log.info(result.toString());
        return result;
    }
}
