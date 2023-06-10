package com.heartsuit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heartsuit.domain.Poetry;
import com.heartsuit.mapper.PoetryMapper;
import com.heartsuit.service.PoetryService;
import org.springframework.stereotype.Service;

/**
 * @Author Heartsuit
 * @Date 2023-06-09
 */
@Service
public class PoetryServiceImpl extends ServiceImpl<PoetryMapper, Poetry> implements PoetryService {
}
