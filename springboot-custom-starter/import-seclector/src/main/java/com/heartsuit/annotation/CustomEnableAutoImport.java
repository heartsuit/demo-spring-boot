package com.heartsuit.annotation;

import com.heartsuit.config.CustomImportSelector;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Author:  Heartsuit
 * Date:  2021/6/28 14:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(CustomImportSelector.class) // 导入自定义的ImportSelector，将自己的类交给IoC管理
public @interface CustomEnableAutoImport {
}
