package com.heartsuit.springbootmybatis.oa.service;

import com.heartsuit.springbootmybatis.oa.entity.Employee;
import com.heartsuit.springbootmybatis.oa.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-08-09
 */
@Service
public class EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    public void save(Employee employee) {
        employeeMapper.save(employee);
    }

    public int update(Employee employee) {
        return employeeMapper.update(employee);
    }

    public int deleteById(Long id) {
        return employeeMapper.deleteById(id);
    }
}
