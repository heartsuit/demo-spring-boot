package com.heartsuit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author Heartsuit
 * @Date 2021-09-01
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOriginPatterns("*").
                        allowedMethods("*").
                        allowedHeaders("*").
                        allowCredentials(true).
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
            }
        };
    }
}

