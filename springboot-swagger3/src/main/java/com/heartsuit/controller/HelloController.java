package com.heartsuit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Heartsuit
 * @Date 2021-08-24
 */
@Api(tags = "测试Controller")
@RestController
public class HelloController {
    @GetMapping("hello")
    @ApiOperation("哈喽")
    public String hello() {
        return "Hello SpringBoot with Swagger3.0";
    }
}
