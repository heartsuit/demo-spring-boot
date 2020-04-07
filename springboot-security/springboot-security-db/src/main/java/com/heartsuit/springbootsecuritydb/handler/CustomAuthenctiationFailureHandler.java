package com.heartsuit.springbootsecuritydb.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenctiationFailureHandler implements AuthenticationFailureHandler {
  @Autowired
  private ObjectMapper mapper;

  public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException auth)
      throws IOException, ServletException {
    System.out.println("登录失败!");
    res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    res.setContentType("application/json;charset=utf-8");
    res.getWriter().write(mapper.writeValueAsString(auth.getMessage()));
  }
}