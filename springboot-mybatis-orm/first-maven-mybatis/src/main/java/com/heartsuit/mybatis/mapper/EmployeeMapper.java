package com.heartsuit.mybatis.mapper;

import com.heartsuit.mybatis.model.Employee;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-03-23
 */
public interface EmployeeMapper {
    /*
     * 新增员工
     * @param employee
     * @return
     * @throws Exception
     */
    public int insertEmployee(Employee employee) throws Exception;

    /*
     * 修改员工
     * @param employee
     * @param id
     * @return
     * @throws Exception
     */
    public int updateEmployee(Employee employee) throws Exception;

    /*
     * 刪除用戶
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteEmployee(Long id) throws Exception;

    /*
     * 根据 id 查询员工信息
     * @param id
     * @return
     * @throws Exception
     */
    public Employee selectEmployeeById(Long id) throws Exception;

    /*
     * 查询所有的员工信息
     * @return
     * @throws Exception
     */
    public List<Employee> selectAllEmployee() throws Exception;
}
