package com.heartsuit.springbootsecuritydb.mapper;

import java.util.List;

import com.heartsuit.springbootsecuritydb.dto.PermissionDto;
import com.heartsuit.springbootsecuritydb.dto.UserDto;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * UserMapper
 */
public interface UserMapper {

    @Select("SELECT * FROM t_user WHERE username = #{username}")
    UserDto getUserByUsername(@Param("username") String username);

    /*
    * SELECT p.* FROM t_permission p LEFT JOIN t_role_permission rp ON p.id = rp.permission_id
    LEFT JOIN t_user_role ur ON rp.role_id = ur.role_id
    LEFT JOIN t_user u ON ur.user_id = u.id
    WHERE u.username = "test";
    * */
    @Select("SELECT p.* FROM t_permission p LEFT JOIN t_role_permission rp ON p.id = rp.permission_id LEFT JOIN t_user_role ur ON rp.role_id = ur.role_id LEFT JOIN t_user u ON ur.user_id = u.id WHERE u.username = #{username};")
    List<PermissionDto> getPermissionsByUsername(@Param("username") String username);
}