package com.heartsuit.mybatis.mapper;

import com.heartsuit.mybatis.model.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    @Insert("insert into employee (name, age, phone) values (#{name}, #{age}, #{phone})")
    int insertEmployee(Employee employee) throws Exception;

    /*
     * 修改员工
     * @param employee
     * @param id
     * @return
     * @throws Exception
     */
    @Update("update employee set age=#{age} where id=#{id}")
    int updateEmployee(Employee employee) throws Exception;

    /*
     * 刪除用戶
     * @param id
     * @return
     * @throws Exception
     */
    @Delete("delete from employee where id=#{id}")
    int deleteEmployee(Long id) throws Exception;

    /*
     * 根据 id 查询员工信息
     * @param id
     * @return
     * @throws Exception
     */
    @Select("select * from employee where id=#{id}")
    Employee selectEmployeeById(Long id) throws Exception;

    /*
     * 查询所有的员工信息
     * @return
     * @throws Exception
     */
    @Select("select * from employee")
    List<Employee> selectAllEmployee() throws Exception;
}
