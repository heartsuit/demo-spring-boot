package com.heartsuit.springbootmybatis.oa.mapper;

import com.heartsuit.springbootmybatis.oa.entity.Employee;
import com.heartsuit.springbootmybatis.oa.utils.MobileNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    void insertBatch() {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();

        //可以执行批量操作的sqlSession, try...with...
        try (SqlSession openSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            long start = System.currentTimeMillis();
            EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
            for (int i = 0; i < 10000; i++) {
                Employee employee = new Employee();
                employee.setName(UUID.randomUUID().toString().substring(0, 6));
                employee.setAge(new Random().nextInt(100));
                employee.setPhone(MobileNumber.generate(0));
                mapper.save(employee);
            }
            openSession.commit();
            long end = System.currentTimeMillis();
            System.out.println("执行时长" + (end - start));
        }
    }
}