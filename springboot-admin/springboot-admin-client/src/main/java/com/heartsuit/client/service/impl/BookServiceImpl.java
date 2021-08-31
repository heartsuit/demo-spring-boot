package com.heartsuit.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heartsuit.client.entity.Book;
import com.heartsuit.client.mapper.BookMapper;
import com.heartsuit.client.service.IBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书籍 服务实现类
 * </p>
 *
 * @author Heartsuit
 * @since 2021-08-25
 */
@Service
@Slf4j
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
