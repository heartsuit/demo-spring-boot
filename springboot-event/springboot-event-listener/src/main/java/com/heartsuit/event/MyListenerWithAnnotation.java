package com.heartsuit.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Author:  Heartsuit
 * Date:  2021/5/11 17:13
 */
@Component
@Slf4j
public class MyListenerWithAnnotation {
    @EventListener
    public void handleEvent(MyEvent event) {
        log.info("MyListenerWithAnnotation, Message received: {}", event.getMsg());
    }
}
