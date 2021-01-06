package com.heartsuit.mapper;

import com.heartsuit.domain.Order;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2020-09-24
 */
@Repository
@Mapper
public interface OrderMapper {
    @Insert("insert into t_order values(#{id}, #{orderType}, #{customerId}, #{amount})")
    void insert(Order order);

    @Select("select * from t_order")
    @Results({
            @Result(column="id", property="id", jdbcType= JdbcType.INTEGER, id=true),
            @Result(column="order_type", property="orderType", jdbcType=JdbcType.INTEGER),
            @Result(column="customer_id", property="customerId", jdbcType=JdbcType.INTEGER),
            @Result(column="amount", property="amount", jdbcType=JdbcType.DOUBLE)
    })
    List<Order> selectAll();

    @Select("select * from t_order WHERE id = #{id}")
    @Results(id = "orderMap", value = {
            @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
            @Result(column = "order_type", property = "orderType", jdbcType = JdbcType.INTEGER),
            @Result(column = "customer_id", property = "customerId", jdbcType = JdbcType.INTEGER),
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.DOUBLE)
    })
    Order selectByOneParam(Integer id);

    @Select("select * from t_order WHERE id = #{id} AND customer_id=#{customerId}")
    @ResultMap(value = "orderMap")
    Order selectByTwoParams(Integer id, Integer customerId);
}
