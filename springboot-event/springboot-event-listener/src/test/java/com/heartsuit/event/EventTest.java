package com.heartsuit.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author:  Heartsuit
 * Date:  2021/5/11 17:10
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EventTest implements ApplicationContextAware {
    private ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Test
    public void publish() {
        context.publishEvent(new MyEvent(this, "Some Message..."));
    }
}
