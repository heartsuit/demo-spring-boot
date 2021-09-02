package com.heartsuit.mybatis.mapper;

import com.heartsuit.mybatis.model.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author Heartsuit
 * @Date 2021-09-02
 */
public class EmployeeMapperTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Before
    public void initialize() {
        // Mybatis 配置文件
        String resource = "mybatis.cfg.xml";

        // 得到配置文件流
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 创建会话工厂，传入 MyBatis 的配置文件信息
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @org.junit.Test
    public void insertEmployee() {
        // 通过工厂得到 SqlSession
        SqlSession session = sqlSessionFactory.openSession();

        EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
        Employee employee = new Employee();
        employee.setName("哈桑");
        employee.setPhone("79363");
        employee.setAge(15);
        try {
            mapper.insertEmployee(employee);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        // 释放资源
        session.close();
    }

    @org.junit.Test
    public void updateEmployee() {
        SqlSession session = sqlSessionFactory.openSession();

        EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
        Employee employee = null;
        try {
            employee = mapper.selectEmployeeById(1240569421151879175L);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        employee.setAge(16);
        try {
            mapper.updateEmployee(employee);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        session.close();
    }

    @org.junit.Test
    public void deleteEmployee() {
        SqlSession session = sqlSessionFactory.openSession();

        EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
        try {
            mapper.deleteEmployee(2L);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        session.close();
    }

    @org.junit.Test
    public void selectEmployeeById() {
        SqlSession session = sqlSessionFactory.openSession();

        EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
        try {
            Employee employee = mapper.selectEmployeeById(1L);
            session.commit();
            System.out.println(employee.getId() + " " + employee.getName() + " "
                    + employee.getPhone() + " " + employee.getAge() + " "
                    + employee.getCreateTime());
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        session.close();
    }

    @org.junit.Test
    public void selectAllEmployee() {
        SqlSession session = sqlSessionFactory.openSession();

        EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
        try {
            List<Employee> employeeList = mapper.selectAllEmployee();
            session.commit();
            for (Employee employee : employeeList) {
                System.out.println(employee.getId() + " " + employee.getName() + " "
                        + employee.getPhone() + " " + employee.getAge() + " "
                        + employee.getCreateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        session.close();
    }
}