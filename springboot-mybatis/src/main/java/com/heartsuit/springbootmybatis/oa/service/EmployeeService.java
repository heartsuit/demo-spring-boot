package com.heartsuit.springbootmybatis.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.heartsuit.springbootmybatis.oa.entity.Employee;
import com.heartsuit.springbootmybatis.oa.mapper.EmployeeMapper;
import com.heartsuit.springbootmybatis.oa.page.PageRequest;
import com.heartsuit.springbootmybatis.oa.page.PageResult;
import com.heartsuit.springbootmybatis.oa.page.PageUtils;
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

    public PageResult findByPage(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        PageHelper.startPage(page, size);
        PageInfo<Employee> employeePageInfo = new PageInfo<>(employeeMapper.findByPage());
        return PageUtils.getPageResult(employeePageInfo);
    }

//    private PageInfo<Employee> getPageInfo(PageRequest pageRequest) {
//        int page = pageRequest.getPage();
//        int size = pageRequest.getSize();
//        PageHelper.startPage(page, size);
//        return new PageInfo<>(employeeMapper.findByPage());
//    }
//
//    public PageResult findByPage(PageRequest pageRequest) {
//        return PageUtils.getPageResult(getPageInfo(pageRequest));
//    }

}
