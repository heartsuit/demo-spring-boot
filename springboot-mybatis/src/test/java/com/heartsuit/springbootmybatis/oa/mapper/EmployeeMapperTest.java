package com.heartsuit.springbootmybatis.oa.mapper;

import com.heartsuit.springbootmybatis.oa.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    void findAll() {
        List<Employee> employeeList = employeeMapper.findAll();
        log.info("All: {}", employeeList);
        assertTrue(employeeList.size() > 0);
    }

    @Test
    void findById() {
        Employee e = employeeMapper.findById(1L);
        log.info("Employee: {}", e);
        assertNotNull(e);
    }
}