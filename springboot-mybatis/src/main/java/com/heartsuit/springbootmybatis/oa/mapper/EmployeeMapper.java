package com.heartsuit.springbootmybatis.oa.mapper;

import com.heartsuit.springbootmybatis.oa.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-03-23
 */
@Mapper
public interface EmployeeMapper {
    List<Employee> findAll();
    Employee findById(Long id);
}
