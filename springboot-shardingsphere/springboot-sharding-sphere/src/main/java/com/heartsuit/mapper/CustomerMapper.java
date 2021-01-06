package com.heartsuit.mapper;

import com.heartsuit.domain.Customer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-09-26
 */
@Repository
@Mapper
public interface CustomerMapper {
    @Insert("insert into t_customer(id,name) values(#{id}, #{name})")
    void insertCustomer(Customer customer);

    @Select("select * from t_customer")
    List<Customer> selectAll();
}
