package com.heartsuit.springbootmybatisplussqlserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heartsuit.springbootmybatisplussqlserver.domain.Material;
import com.heartsuit.springbootmybatisplussqlserver.mapper.MaterialMapper;
import com.heartsuit.springbootmybatisplussqlserver.service.MaterialService;
import org.springframework.stereotype.Service;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
}
