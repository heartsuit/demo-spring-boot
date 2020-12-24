
package com.heartsuit.springbootsecurityform.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

// @Controller
public class CustomErrorController implements ErrorController {
  private static final String ERROR_PATH = "/error";
  private ErrorAttributes errorAttributes;

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

  @Autowired
  CustomErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  // for web page
  @RequestMapping(value = ERROR_PATH, produces = "text/html")
  public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
    int status = response.getStatus();
    return String.valueOf(status);
  }

  // for api
  @RequestMapping(value = ERROR_PATH)
  @ResponseBody
  public String errorApiHandler(HttpServletRequest request, final WebRequest webRequest) {
    Map<String, Object> attr = errorAttributes.getErrorAttributes(webRequest, false);
    HttpStatus status = getStatus(request);
    return status.toString() + ", Message:" + attr.getOrDefault("message", "error");
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.valueOf(statusCode);
  }
}