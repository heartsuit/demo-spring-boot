package com.heartsuit.infrastructure.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.heartsuit.infrastructure.injector.methods.DeleteClickHouse;
import com.heartsuit.infrastructure.injector.methods.UpdateByIdClickHouse;
import com.heartsuit.infrastructure.injector.methods.UpdateClickHouse;

import java.util.List;

/**
 * 注册方法
 *
 * @author liuxiansong
 */
public class ClickHouseSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        /**
         * 这里很重要，先要通过父类方法，获取到原有的集合，不然会自带的通用方法会失效的
         */
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        /***
         * 添加自定义方法类
         */
        methodList.add(new UpdateByIdClickHouse());
        methodList.add(new UpdateClickHouse());
        methodList.add(new DeleteClickHouse());
        return methodList;
    }
}
