package com.heartsuit.infrastructure.config;

import com.heartsuit.infrastructure.injector.ClickHouseSqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClickHouseConfig {
    /**
     * 自定义 SqlInjector
     * 里面包含自定义的全局方法
     */
    @Bean
    public ClickHouseSqlInjector clickHouseSqlInjector() {
        return new ClickHouseSqlInjector();
    }
}
