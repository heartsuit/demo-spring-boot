package com.heartsuit;

import com.heartsuit.bean.BeanClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class ConditionalApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConditionalApplication.class, args);

        // 获取自定义的条件配置动态生成的类
        BeanClass beanClass = applicationContext.getBean(BeanClass.class);
        log.info(beanClass.toString());
    }

}
