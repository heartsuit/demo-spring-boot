package com.heartsuit.springbootrabbit.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private AmqpTemplate template;

    public void send() {
        template.convertAndSend("queue", "hello,rabbit~");
    }

    public void sendExchange() {
        template.convertAndSend("fire.exchange", "fire_queue_key", "hello,rabbit~");
    }

}
