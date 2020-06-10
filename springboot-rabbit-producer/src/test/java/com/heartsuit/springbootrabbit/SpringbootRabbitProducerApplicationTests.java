package com.heartsuit.springbootrabbit;

import com.heartsuit.springbootrabbit.producer.Producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitProducerApplicationTests {

	@Autowired
    private Producer producer;

	@Test
	public void contextLoads() {
		producer.send();
		producer.sendExchange();
	}

}
