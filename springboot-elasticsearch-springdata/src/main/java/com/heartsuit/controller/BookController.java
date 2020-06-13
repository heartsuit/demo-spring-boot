package com.heartsuit.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heartsuit.domain.Book;
import com.heartsuit.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Heartsuit
 * @Date 2020-06-12
 */
@RestController
@RequestMapping("/books")
@Slf4j
public class BookController {
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/all")
    public List<Book> findAll() {
        Iterable<Book> result = bookRepository.findAll();
        Iterator<Book> res = result.iterator();
        List<Book> books = new ArrayList<>();
        while (res.hasNext()) {
            books.add(res.next());
        }
        log.info("List All, Size: {}", books.size());
        return books;
    }

    /**
     * 新增
     *
     * @param book
     * @return
     */
    @PostMapping("/")
    public Book create(@RequestBody Book book) {
        log.info("Saved OK: {}", book.getTitle());
        return bookRepository.save(book);
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Book findById(@PathVariable("id") String id) {
        log.info("Query ID: {}", id);
        Book orElse = bookRepository.findById(id).orElse(null);
        return orElse;
    }

    /**
     * 根据ID修改
     *
     * @param id
     * @param title
     * @param author
     * @param wordCount
     * @param publishDate
     * @return
     * Note: 报错：Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDateTime';
     * 解决：在参数前添加注解：@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     */
    @PutMapping("/{id}")
    public Book update(@PathVariable("id") String id,
                       @RequestParam(name = "title", required = false) String title,
                       @RequestParam(name = "author", required = false) String author,
                       @RequestParam(name = "wordCount", required = false) Integer wordCount,
                       @RequestParam(name = "publishDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publishDate) {
        Optional<Book> optional = bookRepository.findById(id);
        if (optional.isPresent()) {
            Book book = optional.get();
            if (title != null) {
                book.setTitle(title);
            }
            if (author != null) {
                book.setAuthor(author);
            }
            if (wordCount != null) {
                book.setWordCount(wordCount);
            }
            if (publishDate != null) {
                book.setPublishDate(publishDate);
            }
            return bookRepository.save(book);
        }
        return null;
    }

    /**
     * 根据ID删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        log.info("Deleted ID: {}", id);
        bookRepository.deleteById(id);
    }

    /**
     * 删除所有
     */
    @PostMapping("/clear")
    public void clear() {
        log.info("Delete All!");
        bookRepository.deleteAll();
    }

    /**
     * 根据作者查询
     *
     * @param author
     * @return
     */
    @PostMapping("/author")
    public List<Book> byAuthor(String author) {
        log.info("By Author");
        return bookRepository.findByAuthor(author);
    }

    /**
     * 根据作者检索
     *
     * @param author
     * @return
     */
    @PostMapping("/author/like")
    public List<Book> byAuthorLike(String author) {
        log.info("By Author Like");
        return bookRepository.findByAuthorLike(author);
    }

    /**
     * 根据标题查询
     *
     * @param title
     * @return
     */
    @PostMapping("/title")
    public List<Book> byTitle(String title) {
        log.info("By Title");
        return bookRepository.findByTitle(title);
    }

    /**
     * 根据字数查询
     *
     * @param wordCount
     * @return
     */
    @GetMapping("/wordCount/{count}")
    public List<Book> byWordCount(@PathVariable("count") Integer wordCount) {
        log.info("By WordCount");
        return bookRepository.findByWordCount(wordCount);
    }

    /**
     * 根据作者与标题查询
     *
     * @param author
     * @param title
     * @return
     */
    @PostMapping("/query")
    public List<Book> byAuthorAndTitle(String author, String title) {
        log.info("By AuthorAndTitle");
        return bookRepository.findByAuthorAndTitle(author, title);
    }

    /**
     * 根据标题查询@Query
     *
     * @param title
     * @return
     */
    @PostMapping("/query/title")
    public List<Book> queryByTitle(String title) {
        log.info("Query By Title");
        return bookRepository.queryByTitle(title);
    }
}
