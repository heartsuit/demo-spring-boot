package com.heartsuit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heartsuit.entity.Employee;
import com.heartsuit.mapper.EmployeeMapper;
import com.heartsuit.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Author Heartsuit
 * @Date 2022-11-15
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
