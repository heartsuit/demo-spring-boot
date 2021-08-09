package com.heartsuit.springbootmybatis.oa.mapper;

import com.heartsuit.springbootmybatis.oa.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-03-23
 */
@Mapper
@Repository
public interface EmployeeMapper {
    List<Employee> findAll();
    Employee findById(Long id);
    void save(Employee employee);
    int update(Employee employee);
    int deleteById(long id);
}
