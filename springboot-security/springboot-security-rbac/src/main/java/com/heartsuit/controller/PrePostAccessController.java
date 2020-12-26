package com.heartsuit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @Author Heartsuit
 * @Date 2020-12-19
 */
@RestController
@RequestMapping("/user1")
@Slf4j
public class PrePostAccessController {
    @GetMapping(value = "/add")
//    @PreAuthorize("hasRole('ROLE_dev')")
    @PreAuthorize("hasRole('dev')")
//    @PreAuthorize("hasAnyRole('ROLE_dev', 'ROLE_test')")
//    @PreAuthorize("hasRole('ROLE_dev') and hasRole('ROLE_test')")
    public String accessResource1() {
        return " Access Resource 1: Add User";
    }

    @GetMapping(value = "/query")
    @PreAuthorize("hasAuthority('ROLE_test')")
    public String accessResource2() {
        return " Access Resource 2: Query User";
    }

    @GetMapping(value = "/")
//    @PreAuthorize("authenticated")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping(value = "/res")
    @PostAuthorize("returnObject==true")
    public boolean response() {
        int i = new Random().nextInt();
        log.info("Response, {}", i);
        return i > 0;
    }
}
