package com.taosdata.example.mybatisplusdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Heartsuit
 * @Date 2022-12-30
 */
@ConfigurationProperties(prefix = "td.consumer")
@Component
@Data
public class TdConfigProperties {
    private String bootstrapServers;
    private Boolean msgWithTableName;
    private Boolean enableAutoCommit;
    private String groupId;
    private String valueDeserializer;
}
