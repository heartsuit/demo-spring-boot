package com.heartsuit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
@Configuration
@ConfigurationProperties(prefix = "pdf.watermark")
@Data
public class PdfConfigProperties
{
    private TextProperties text = new TextProperties();

    @Data
    public static class TextProperties{
        private Boolean enabled;
        private String content;
    }

}