package com.heartsuit.config;

import com.heartsuit.bean.FirstClass;
import com.heartsuit.bean.SecondClass;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Author:  Heartsuit
 * Date:  2021/6/28 14:43
 */
public class CustomImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{FirstClass.class.getName(), SecondClass.class.getName()};
    }
}
