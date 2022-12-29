package com.heartsuit.springbootmybatisplussqlserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.springbootmybatisplussqlserver.domain.Material;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
}
