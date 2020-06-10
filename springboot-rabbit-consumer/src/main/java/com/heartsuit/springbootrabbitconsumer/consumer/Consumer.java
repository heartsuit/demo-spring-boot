package com.heartsuit.springbootrabbitconsumer.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    // 监听延迟消息队列
    @RabbitListener(queues = "delayed_msg_queue")
    public void receiveDelayedMessage(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("The moment Delayed message Received: " + sdf.format(new Date()) + ", and message body is: " + msg);
    }
}
