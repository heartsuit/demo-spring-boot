package com.taosdata.example.mybatisplusdemo.consumer;

import com.taosdata.example.mybatisplusdemo.config.TdConfigProperties;
import com.taosdata.example.mybatisplusdemo.domain.Power;
import com.taosdata.jdbc.tmq.ConsumerRecords;
import com.taosdata.jdbc.tmq.TMQConstants;
import com.taosdata.jdbc.tmq.TaosConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @Author Heartsuit
 * @Date 2022-12-30
 */
@Configuration
@Slf4j
public class SubscribeOnStartUp {
    private static final String TOPIC = "alarm_temperature";

    @Autowired
    private TdConfigProperties tdConfig;

    @Bean
    public Properties config() {
        Properties properties = new Properties();
        properties.setProperty(TMQConstants.BOOTSTRAP_SERVERS, tdConfig.getBootstrapServers());
        properties.setProperty(TMQConstants.MSG_WITH_TABLE_NAME, tdConfig.getMsgWithTableName().toString());
        properties.setProperty(TMQConstants.ENABLE_AUTO_COMMIT, tdConfig.getEnableAutoCommit().toString());
        properties.setProperty(TMQConstants.GROUP_ID, tdConfig.getGroupId());
        properties.setProperty(TMQConstants.VALUE_DESERIALIZER, tdConfig.getValueDeserializer());
        return properties;
    }

    @PostConstruct
    public void subscribe() {
        log.info("PostConstruct");
        // poll data
        TaosConsumer<Power> consumer = null;
        try {
            consumer = new TaosConsumer<>(config());
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                ConsumerRecords<Power> powerRecords = consumer.poll(Duration.ofMillis(100));
                for (Power power : powerRecords) {
                    // TODO 告警推送：短信、邮箱、钉钉、企业微信、飞书、WebHook
                    log.info("Consumed: {}", power.toString());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (null != consumer) {
                    consumer.unsubscribe();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
