package com.heartsuit.controller;

import com.alibaba.fastjson.JSON;
import com.heartsuit.domain.Book;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
@RestController
@RequestMapping("/another-book")
public class EsController {
    private final RestHighLevelClient client;

    public EsController(RestHighLevelClient client) {
        this.client = client;
    }

    private String INDEX = "book";

    @GetMapping("/{id}")
    public Book get(@PathVariable(name = "id") String id) throws IOException {
        GetRequest getRequest = new GetRequest(INDEX, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        return JSON.parseObject(response.getSourceAsString(), Book.class);
    }

    @PostMapping("/")
    public ResponseEntity add(@RequestParam("author") String author,
                              @RequestParam("title") String title,
                              @RequestParam("wordCount") Integer wordCount,
                              @RequestParam("publishDate")
                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime publishDate
    ) {
        IndexRequest request = new IndexRequest(INDEX);
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("author", author)
                    .field("title", title)
                    .field("word_count", wordCount)
                    .field("publish_date", publishDate)
                    .endObject();

            request.opType("index").source(builder);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return new ResponseEntity(response.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable(name = "id") String id) {
        DeleteRequest request = new DeleteRequest(INDEX, id);
        DeleteResponse response = null;
        try {
            response = client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(response.getResult(), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity update(@RequestParam(name = "id") String id,
                                 @RequestParam(name = "title", required = false) String title,
                                 @RequestParam(name = "author", required = false) String author) {
        UpdateRequest request = new UpdateRequest(INDEX, id);
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject();
            if (!StringUtils.isEmpty(title)) {
                builder.field("title", title);
            }
            if (!StringUtils.isEmpty(author)) {
                builder.field("author", author);
            }
            builder.endObject();
            request.doc(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UpdateResponse response = null;
        try {
            response = client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(response.getResult(), HttpStatus.OK);
    }

    @PostMapping("/query")
    public ResponseEntity query(@RequestParam(value = "author", required = false) String author,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "gt_word_count", defaultValue = "0") int gtWordCount,
                                @RequestParam(value = "lt_word_count", required = false) Integer ltWordCount
    ) {
        SearchRequest request = new SearchRequest(INDEX);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(author)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }
        if (!StringUtils.isEmpty(title)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("word_count")
                .from(gtWordCount);
        if (ltWordCount != null && ltWordCount > 0) {
            rangeQueryBuilder.to(ltWordCount);
        }

        boolQueryBuilder.filter(rangeQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0).size(10).query(boolQueryBuilder);
        System.out.println(searchSourceBuilder);

        request.searchType(SearchType.DEFAULT).source(searchSourceBuilder);

        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
            for (SearchHit s : searchResponse.getHits().getHits()) {
                list.add(s.getSourceAsMap());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }
}
