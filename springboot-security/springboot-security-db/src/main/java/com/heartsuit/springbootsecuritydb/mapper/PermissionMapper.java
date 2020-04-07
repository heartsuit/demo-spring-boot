package com.heartsuit.springbootsecuritydb.mapper;

import java.util.List;

import com.heartsuit.springbootsecuritydb.dto.PermissionDto;

import org.apache.ibatis.annotations.Select;

public interface PermissionMapper {

  @Select("SELECT * FROM t_permission")
  List<PermissionDto> getAllPermissions();
}