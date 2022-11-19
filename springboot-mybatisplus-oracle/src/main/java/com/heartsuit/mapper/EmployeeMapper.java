package com.heartsuit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Heartsuit
 * @Date 2022-11-15
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
