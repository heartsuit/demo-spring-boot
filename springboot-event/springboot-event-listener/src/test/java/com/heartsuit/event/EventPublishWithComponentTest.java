package com.heartsuit.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author:  Heartsuit
 * Date:  2021-9-3
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EventPublishWithComponentTest {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void publish() {
        applicationEventPublisher.publishEvent(new MyEvent(this, "Some Message..."));
    }
}
