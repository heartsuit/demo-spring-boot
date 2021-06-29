package com.heartsuit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Author:  Heartsuit
 * Date:  2021/6/28 14:43
 */
@Slf4j
public class CustomCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String os = conditionContext.getEnvironment().getProperty("os.name");
        assert os != null;
        log.info(os);
        // 为Windows操作系统时返回true，进行自动配置
        return os.contains("Windows");
    }
}
