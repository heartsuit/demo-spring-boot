package com.heartsuit.springbootsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  //Spring Security provide /login and /logout mappings by default

  @PostMapping(value = "/greeting")
  public String loginSuccess() {
    return "Welcome";
  }

  @GetMapping(value = "/user/add")
  public String accessResource1() {
    return " Access Resource 1: Add User";
  }

  @GetMapping(value = "/user/query")
  public String accessResource2() {
    return " Access Resource 2: Query User";
  }
}
