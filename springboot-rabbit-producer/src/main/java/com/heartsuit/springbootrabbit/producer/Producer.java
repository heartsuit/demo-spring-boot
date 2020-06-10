package com.heartsuit.springbootrabbit.producer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     * 发送延迟消息
     * Note: 在发送的时候，必须加上一个header: x-delay
     *
     * @param msg
     */
    public void sendDelayedMessage(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("The moment sending message: " + sdf.format(new Date()));
        template.convertAndSend("delayed_msg_exchange", "delayed_msg_key", msg, message -> {
            message.getMessageProperties().setHeader("x-delay", 5000); // 延迟5s发送
            return message;
        });
    }
}
