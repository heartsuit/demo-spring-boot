package com.heartsuit.springbootsecurityform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping(value = "/greeting")
  public String loginSuccess() {
    return "index";
  }

  @GetMapping(value = "/user/add")
  @ResponseBody
  public String accessResource1() {
    return " Access Resource 1: Add User";
  }

  @GetMapping(value = "/user/query")
  @ResponseBody
  public String accessResource2() {
    return " Access Resource 2: Query User";
  }

  @RequestMapping(value = "/hi", method=RequestMethod.POST)
  @ResponseBody
  public String post() {
    return "hi post";
  }
}
