package com.heartsuit.springbootsecuritysession.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
  
  @Value("${server.port}")
  Integer port;

  @GetMapping(value = "/")
  public String greeting() {
    return String.valueOf(port);
  }

  @GetMapping(value = "/session/set")
  public String setSession(HttpSession session) {
    session.setAttribute("key", "value");
    return port + ": Session updated.";
  }

  @GetMapping(value = "/session/get")
  public String getSession(HttpSession session) {
    Object value = session.getAttribute("key");
    return port + "：" + (String) value;
  }
}
