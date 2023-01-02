package com.taosdata.example.mybatisplusdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taosdata.example.mybatisplusdemo.domain.Power;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@Mapper
@Repository
public interface PowerMapper extends BaseMapper<Power> {
}
