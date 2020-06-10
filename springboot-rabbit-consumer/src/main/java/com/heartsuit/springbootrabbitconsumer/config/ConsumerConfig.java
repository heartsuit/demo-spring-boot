package com.heartsuit.springbootrabbitconsumer.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
     // 采用默认的exchange
     @Bean
     public Queue queue() {
          return new Queue("queue");
     }

     // 采用exchange，指定路由key
     @Bean(name = "fire")
     public Queue fireQueue() {
          return new Queue("fire_queue");
     }

     // 采用exchange，通配路由key
     @Bean(name = "fire-wildcard")
     public Queue firesQueue() {
          return new Queue("fire_wildcard_queue");
     }

     @Bean
     public TopicExchange exchange() {
          return new TopicExchange("fire.exchange");
     }

     @Bean
     public Binding bindingExchangeFire(@Qualifier("fire") Queue queue, TopicExchange exchange) {
          return BindingBuilder.bind(queue).to(exchange).with("fire_queue_key");
     }

     @Bean
     public Binding bindingExchangeFireWildCard(@Qualifier("fire-wildcard") Queue queue, TopicExchange exchange) {
          return BindingBuilder.bind(queue).to(exchange).with("#");
     }

     // 延迟消息queue
     @Bean
     public Queue delayedMessageQueue() {
          Queue queue = new Queue("delayed_msg_queue", true);
          return queue;
     }

     // 延迟消息exchange, Note: CustomExchange的类型必须是x-delayed-message
     @Bean
     public CustomExchange delayExchange() {
          Map<String, Object> args = new HashMap<>();
          args.put("x-delayed-type", "direct");
          return new CustomExchange("delayed_msg_exchange", "x-delayed-message", true, false, args);
     }

     @Bean
     public Binding binding() {
          return BindingBuilder.bind(delayedMessageQueue()).to(delayExchange()).with("delayed_msg_key").noargs();
     }
}