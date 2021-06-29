package com.heartsuit.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Author:  Heartsuit
 * Date:  2021/6/28 15:29
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties(RedissonProperties.class)
@Slf4j
public class RedissonAutoConfiguration {
    @Bean
    RedissonClient redissonClient(RedissonProperties redissonProperties) {
        log.info("Loaded Configuration: {}", redissonProperties.toString());
        Config config = new Config();
        String prefix = "redis://";
        if (redissonProperties.isSsl()) {
            prefix = "rediss://";
        }

        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(prefix + redissonProperties.getHost() + ":" + redissonProperties.getPort())
                .setConnectTimeout(redissonProperties.getTimeout())
                .setDatabase(redissonProperties.getDatabase());

        if (!StringUtils.isEmpty(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
