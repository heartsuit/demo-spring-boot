package com.heartsuit.client.controller;

import com.heartsuit.client.entity.Book;
import com.heartsuit.client.service.IBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 书籍 前端控制器
 * </p>
 *
 * @author Heartsuit
 * @since 2021-08-25
 */
@RestController
@RequestMapping("book")
@Slf4j
public class BookController {

    @Autowired
    private IBookService bookService;

    @GetMapping("list")
    public List<Book> list() {
        List<Book> list = bookService.list();
        log.info("Result: {}", list.toString());
        return list;
    }
}
