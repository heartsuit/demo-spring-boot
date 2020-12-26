package com.heartsuit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Heartsuit
 * @Date 2020-12-19
 */
@RestController
@Slf4j
public class RoleAccessController {
    @GetMapping(value = "/user/add")
    public String accessResource1() {
        return " Access Resource 1: Add User";
    }

    @GetMapping(value = "/user/query")
    public String accessResource2() {
        return " Access Resource 2: Query User";
    }

    @GetMapping(value = "/")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }
}
