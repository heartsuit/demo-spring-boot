package com.heartsuit.controller;

import com.heartsuit.domain.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author Heartsuit
 * @Date 2021-07-31
 */
@RestController
@RequestMapping("/original")
public class OriginalController {
    @GetMapping("/hello")
    public String getStr() {
        return "Hello SpringBoot";
    }

    @GetMapping("/account")
    public Account getAccount() {
        Account Account = new Account(1, "ICBC110", "pig", BigDecimal.TEN);
        return Account;
    }

    @GetMapping("/error")
    public int error() {
        int i = 2 / 0;
        return i;
    }
}
