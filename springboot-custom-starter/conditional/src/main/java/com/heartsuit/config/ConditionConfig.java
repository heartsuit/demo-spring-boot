package com.heartsuit.config;

import com.heartsuit.bean.BeanClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Author:  Heartsuit
 * Date:  2021/6/28 15:03
 */
@Configuration
public class ConditionConfig {
    @Bean
    @Conditional(CustomCondition.class)
    public BeanClass beanClass(){
        return new BeanClass();
    }
}
