package com.heartsuit.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Author:  Heartsuit
 * Date:  2021/5/11 17:13
 */
@Component
@Slf4j
public class MyListenerWithInterface implements ApplicationListener<MyEvent> {
    @Override
    public void onApplicationEvent(MyEvent event) {
        log.info("MyListenerWithInterface, Message received: {}", event.getMsg());
    }
}
