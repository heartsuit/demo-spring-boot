package com.heartsuit.client.config;

import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 配置显示Web-HttpTraces
 * @Author Heartsuit
 * @Date 2021-08-31
 */
@Configuration
public class SpringBootAdminConfig {
    @Bean
    public InMemoryHttpTraceRepository getInMemoryHttpTrace(){
        return new InMemoryHttpTraceRepository();
    }
}
