package com.heartsuit.controller;

import com.heartsuit.service.ElasticSearchService;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    private ElasticSearchService elasticSearchService;

    public IndexController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/create")
    public boolean createIndex(@RequestParam(name = "index") String index) {
        return elasticSearchService.createIndex(index);
    }

    @DeleteMapping("/delete")
    public boolean deleteIndex(@RequestParam(name = "index") String index) {
        return elasticSearchService.deleteIndex(index);
    }
}
