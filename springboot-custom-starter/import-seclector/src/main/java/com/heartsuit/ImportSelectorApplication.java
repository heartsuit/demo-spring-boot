package com.heartsuit;

import com.heartsuit.annotation.CustomEnableAutoImport;
import com.heartsuit.bean.FirstClass;
import com.heartsuit.bean.SecondClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@CustomEnableAutoImport // 使用自定义注解
public class ImportSelectorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ImportSelectorApplication.class, args);

        // 获取自定义的两个类
        FirstClass firstClass = applicationContext.getBean(FirstClass.class);
        System.out.println(firstClass);

        SecondClass secondClass = applicationContext.getBean(SecondClass.class);
        System.out.println(secondClass);
    }

}
