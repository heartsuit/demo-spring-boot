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

    @Test
    void save() {
        Employee employee = new Employee();
        employee.setName("阿拉斯加");
        employee.setAge(12);
        employee.setPhone("12345678901");
        employeeMapper.save(employee);
    }

    @Test
    void update() {
        Employee employee = employeeMapper.findById(1240569421151879172L);
        employee.setAge(34);
        int updated = employeeMapper.update(employee);
        assertTrue(updated > 0);
    }

    @Test
    void delete() {
        int deleted = employeeMapper.deleteById(1240569421151879170L);
        assertTrue(deleted > 0);
    }
}