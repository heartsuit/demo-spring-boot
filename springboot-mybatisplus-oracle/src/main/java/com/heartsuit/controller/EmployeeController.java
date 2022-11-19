package com.heartsuit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heartsuit.entity.Employee;
import com.heartsuit.service.EmployeeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2022-11-15
 */
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("list")
    public List<Employee> list(){
        return employeeService.list();
    }

    @PostMapping("save")
    public boolean save(){
        Employee employee = new Employee();
        employee.setEmpno(6666);
        employee.setEname("John");
        employee.setJob("PM");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);

        return employeeService.save(employee);
    }

    @PutMapping("update")
    public boolean update(){
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<>();
        return employeeService.update(updateWrapper.lambda().set(Employee::getJob, "CTO").eq(Employee::getEmpno, 6666));
    }

    @DeleteMapping("delete/{id}")
    public boolean deleteByCondition(@PathVariable Integer id){
        return employeeService.removeById(id);
    }

    @DeleteMapping("deleteByCondition")
    public boolean deleteByCondition(){
        return employeeService.remove(new QueryWrapper<Employee>().lambda().eq(Employee::getEmpno, 6666));
    }

    @PostMapping("saveTransaction")
    @Transactional
    public boolean saveWithTransaction(){
        Employee employee = new Employee();
        employee.setEmpno(7777);
        employee.setEname("Wick");
        employee.setJob("CEO");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);
        employeeService.save(employee);

        // Exception
        int x = 1/0;

        employee.setEmpno(8888);
        employee.setEname("Tada");
        employee.setJob("CFO");
        employee.setMgr(7782);
        employee.setHiredate(new Date());
        employee.setSal(1000);
        employee.setComm(0);
        employee.setDeptno(10);

        return employeeService.save(employee);
    }
}
