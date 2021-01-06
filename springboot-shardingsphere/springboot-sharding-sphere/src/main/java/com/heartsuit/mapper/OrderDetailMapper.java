package com.heartsuit.mapper;

import com.heartsuit.domain.Order;
import com.heartsuit.domain.OrderDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author Heartsuit
 * @Date 2020-09-26
 */
@Repository
@Mapper
public interface OrderDetailMapper {

    @Insert("insert into t_order_detail values(#{id},#{detail},#{orderId})")
    void insertOrderDetail(OrderDetail orderDetail);

    @Select("select * from t_order_detail where id = #{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "detail",column = "detail"),
            @Result(property = "orderId",column = "order_id")
    })
    OrderDetail selectById(Integer id);

    @Select("select * from t_order where id = #{id} and customer_id=#{customerId}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "orderType",column = "order_type"),
            @Result(property = "customerId",column = "customer_id"),
            @Result(property = "amount",column = "amount")
    })
    Order selectByTwoParams(Integer id, Integer customerId);

    @Select("select d.* from t_order o join t_order_detail d on o.id = d.order_id where o.id=#{orderId}")
//    @Select("select d.* from t_order_detail d join t_order o on o.id = d.order_id where o.id=#{orderId}") // 笛卡尔积
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "detail",column = "detail"),
            @Result(property = "orderId",column = "order_id")
    })
    OrderDetail selectByJoinTable(Integer orderId);
}
