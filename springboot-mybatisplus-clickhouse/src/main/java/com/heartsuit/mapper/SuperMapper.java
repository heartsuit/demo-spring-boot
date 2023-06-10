package com.heartsuit.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * @author liuxiansong
 * 自定义方法
 */
@SuppressWarnings("all")
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * @return
     * @Description: 删除并填充删除人信息
     * @param: id 主键id
     * @auther: zpq
     * @date: 2020/11/10 11:47 上午
     */
    boolean updateByIdClickHouse(@Param("et") T entity);


    /**
     * @return
     * @Description: 删除并填充删除人信息
     * @param: id 主键id
     * @auther: zpq
     * @date: 2020/11/10 11:47 上午
     */
    boolean updateClickHouse(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);

    /**
     * 主键删除
     *
     * @param id
     * @return
     */
    int deleteByIdClickHouse(Serializable id);
}
