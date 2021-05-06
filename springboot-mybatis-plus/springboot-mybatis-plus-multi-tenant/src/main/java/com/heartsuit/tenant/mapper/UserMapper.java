package com.heartsuit.tenant.mapper;

import java.util.List;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.heartsuit.tenant.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * MP 支持不需要 UserMapper.xml 这个模块演示内置 CRUD 咱们就不要 XML 部分了
 * </p>
 *
 * @author hubin
 * @since 2018-08-11
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 自定义SQL：默认也会增加多租户条件
     * 参考打印的SQL
     * @return
     */
//    @Select("select count(1) from user")
//    @SqlParser(filter = true) // @SqlParser(filter = true)注解代表不进行SQL解析也就没有租户的附加条件。
    Integer myCount();

    List<User> getUserAndAddr(@Param("username") String username);

    List<User> getAddrAndUser(@Param("name") String name);
}
