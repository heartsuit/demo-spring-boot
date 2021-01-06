package com.heartsuit.mapper;

import com.heartsuit.domain.Order;
import com.heartsuit.domain.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OrderDetailMapperTest {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Test
    void insertOrderDetail() {
        for (int i = 1; i <= 10 ; i++) {
            OrderDetail ordersDetail = new OrderDetail();

            ordersDetail.setId(i);
            ordersDetail.setDetail("detail_"+i);
            ordersDetail.setOrderId(i);
            orderDetailMapper.insertOrderDetail(ordersDetail);
        }
    }

    @Test
    void selectById() {
        OrderDetail orderDetail = orderDetailMapper.selectById(1);
        log.info(orderDetail.toString());
    }

    @Test
    void selectByTwoParams() {
        Order order = orderDetailMapper.selectByTwoParams(1, 1);
        log.info(order.toString());
    }

    @Test
    void selectByJoinTable() {
        OrderDetail detail = orderDetailMapper.selectByJoinTable(4);
        log.info(detail.toString());
    }
}