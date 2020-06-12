package com.heartsuit.controller;

import com.heartsuit.domain.Book;
import com.heartsuit.service.ElasticSearchService;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
@RestController
@RequestMapping("/book")
public class DocumentController {
    private static final String INDEX = "book";

    private final ElasticSearchService elasticSearchService;

    public DocumentController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/{id}")
    public int insertDocumentById(@PathVariable(name = "id") Long id, @RequestBody Book book) {
        return elasticSearchService.insertDocument(INDEX, book, XContentType.JSON, id.toString());
    }

    @GetMapping("/{id}")
    public Book GetDocumentById(@PathVariable(name = "id") Long id) {
        return elasticSearchService.getDocument(INDEX, id.toString(), Book.class);
    }

    @PutMapping("/{id}")
    public int updateDocumentById(@PathVariable(name = "id") Long id, @RequestBody Book book){
        return elasticSearchService.updateDocument(INDEX, book, XContentType.JSON, id.toString());
    }

    @DeleteMapping("/{id}")
    public int deleteDocumentById(@PathVariable(name = "id") Long id){
        return elasticSearchService.deleteDocument(INDEX, id.toString());
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchDocument(){
        return elasticSearchService.searchDocument(INDEX);
    }

    @PostMapping("/batch")
    public boolean batchInsertDocument(@RequestBody List<Book> books){
        return elasticSearchService.batchInsertDocument(INDEX, books, XContentType.JSON);
    }
}
