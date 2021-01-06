package com.heartsuit.mapper;

import com.heartsuit.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void insertCustomer() {
        for (int i = 1; i <= 10; i++) {
            Customer customer = new Customer();
            customer.setId(i);
            customer.setName(UUID.randomUUID().toString());
            customerMapper.insertCustomer(customer);
            log.info(i + " written");
        }
    }

    @Test
    void selectAll() {
        List<Customer> customers = customerMapper.selectAll();
        log.info(customers.toString());
    }
}