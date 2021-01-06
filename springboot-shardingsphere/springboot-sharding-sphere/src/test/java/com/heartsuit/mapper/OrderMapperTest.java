package com.heartsuit.mapper;

import com.heartsuit.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void insert() {
        for (int i = 1; i <= 10; i++) {
            Order order = new Order();
            order.setId(i);
            order.setOrderType(i);
            order.setCustomerId(new Random().nextInt(10));
            order.setAmount(i + i * 1.0);
            orderMapper.insert(order);
            System.out.println(i + " written");
        }
    }

    @Test
    void selectAll() {
        List<Order> orders = orderMapper.selectAll();
        System.out.println(orders);
    }

    @Test
    void selectByOneParam() {
        Order order = orderMapper.selectByOneParam(2);
        System.out.println(order);
    }

    @Test
    void selectByTwoParam() {
        Order order = orderMapper.selectByTwoParams(2, 2);
        System.out.println(order);
    }
}