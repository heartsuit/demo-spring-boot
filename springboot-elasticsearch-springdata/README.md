### 背景

[上一篇](https://blog.csdn.net/u013810234/article/details/107008689)使用`HighLevelClient`的方式实现了`SpringBoot`集成`ElasticSearch`。今天换作`SpringDataElasticsearch`来完成`SpringBoot`与`ElasticSearch`的集成。SpringData系列借助自定义的命名规则，直接定义完接口，它可以帮助我们自动实现接口。

### 核心依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### 领域模型

```java
package com.heartsuit.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2020-06-12
 */
@Data
//@Document(indexName = "book", useServerConfiguration = true, createIndex = false)
@Document(indexName = "book")
public class Book {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String author;

    @Field(name = "word_count", type = FieldType.Integer)
    private Integer wordCount;

    /**
     * 1. Jackson日期时间序列化问题：
     * Cannot deserialize value of type `java.time.LocalDateTime` from String "2020-06-04 15:07:54": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2020-06-04 15:07:54' could not be parsed at index 10
     * 解决：@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     * 2. 日期在ES存为long类型
     * 解决：需要加format = DateFormat.custom
     * 3. java.time.DateTimeException: Unable to obtain LocalDate from TemporalAccessor: {DayOfMonth=5, YearOfEra=2020, MonthOfYear=6},ISO of type java.time.format.Parsed
     * 解决：pattern = "uuuu-MM-dd HH:mm:ss" 即将yyyy改为uuuu，或8uuuu: pattern = "8uuuu-MM-dd HH:mm:ss"
     * 参考：https://www.elastic.co/guide/en/elasticsearch/reference/current/migrate-to-java-time.html#java-time-migration-incompatible-date-formats
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(name = "publish_date", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime publishDate;
}
```

### 核心方法

```java
package com.heartsuit.repository;

import java.util.List;
import com.heartsuit.domain.Book;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author Heartsuit
 * @Date 2020-06-12
 */
public interface BookRepository extends ElasticsearchRepository<Book, String> {
  List<Book> findByAuthor(String author);
  List<Book> findByAuthorLike(String author);
  List<Book> findByTitle(String author);
  List<Book> findByWordCount(Integer wordCount);
  List<Book> findByAuthorAndTitle(String author, String title);

  @Query("{\"bool\" : {\"must\" : {\"match\" : {\"title\" : \"?0\"}}}}")
  List<Book> queryByTitle(String keyword);
}
```

### 测试接口

```java
package com.heartsuit.controller;

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
```

### Source Code

[Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-elasticsearch-springdata)

Done~😎