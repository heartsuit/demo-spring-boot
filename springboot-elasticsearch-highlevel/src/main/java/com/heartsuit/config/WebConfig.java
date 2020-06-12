package com.heartsuit.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-06-05
 */
@Configuration
@EnableWebMvc // 必须要有这个注解，否则报错：JSON parse error: Cannot deserialize value of type `java.time.LocalDateTime` from String \"2020-06-04 15:07:54\": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2020-06-04 15:07:54' could not be parsed at index 10; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.time.LocalDateTime` from String \"2020-06-04 15:07:54\": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2020-06-04 15:07:54' could not be parsed at index 10\n at [Source: (PushbackInputStream); line: 5, column: 17] (through reference chain: com.heartsuit.domain.Book[\"publishDate\"]
public class WebConfig implements WebMvcConfigurer {
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setCharset(Charset.forName("UTF-8"));
        config.setDateFormat("yyyyMMdd HH:mm:ssS");
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        fastJsonConverter.setFastJsonConfig(config);
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON);
        fastJsonConverter.setSupportedMediaTypes(list);
        converters.add(fastJsonConverter);
    }
}