package com.heartsuit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Heartsuit
 * @Date 2020-11-29
 */
@Controller
public class HelloController {
    @RequestMapping("/")
    public String hello(){
        return "index";
    }

    @GetMapping("/")
    @ResponseBody
    public String test(){
        return "test";
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public String token(){
        return "token";
    }
}
