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