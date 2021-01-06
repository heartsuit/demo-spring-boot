package com.heartsuit.mapper;

import com.heartsuit.domain.DictOderType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author Heartsuit
 * @Date 2020-09-26
 */
@Repository
@Mapper
public interface DictOrderTypeMapper {
    @Insert("insert into dict_order_type values(#{id}, #{orderType})")
    void insertDictOrderType(DictOderType dictOderType);
}
