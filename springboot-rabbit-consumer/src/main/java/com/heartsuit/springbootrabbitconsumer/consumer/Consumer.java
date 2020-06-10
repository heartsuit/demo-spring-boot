package com.heartsuit.springbootrabbitconsumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    @RabbitListener(queues = "queue")
    public void receive(String str) {
        System.out.println("Receive: " + str);
    }

    @RabbitListener(queues = "fire_queue")
    public void process1(String str) {
        System.out.println("message via exchange: " + str);
    }

    @RabbitListener(queues = "fire_wildcard_queue")
    public void process2(String str) {
        System.out.println("message via exchange with wildcard key: " + str);
    }
}
