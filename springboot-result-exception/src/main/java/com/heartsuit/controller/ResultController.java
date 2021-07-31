package com.heartsuit.controller;

import com.heartsuit.domain.Account;
import com.heartsuit.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author Heartsuit
 * @Date 2021-07-31
 */
@RestController
@RequestMapping("result")
public class ResultController {
    @GetMapping("/hello")
    public Result<String> getStr(){
        return Result.success("Hello SpringBoot");
    }

    @GetMapping("/account")
    public Result<Account> getAccount(){
        Account Account = new Account(1,"ICBC110", "pig", BigDecimal.TEN);
        return Result.success(Account);
    }

    @GetMapping("/error")
    public int error(){
        int i = 2/0;
        return i;
    }
}
