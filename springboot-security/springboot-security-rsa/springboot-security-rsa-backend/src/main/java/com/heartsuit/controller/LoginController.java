package com.heartsuit.controller;

/**
 * @Author Heartsuit
 * @Date 2021-09-01
 */

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.heartsuit.utils.RSAEncrypt;
import com.heartsuit.vo.FormUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
@Slf4j
public class LoginController {
    @Value("${rsa.private_key}")
    private String privateKey;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    public String login(@RequestBody FormUser formUser, HttpServletRequest request) {
        log.info("formUser encrypted: {}", formUser);

        // 用户信息RSA私钥解密，方法一：自定义工具类：RSAEncrypt
//        String username = RSAEncrypt.decrypt(formUser.getUsername(), privateKey);
//        String password = RSAEncrypt.decrypt(formUser.getPassword(), privateKey);
//        log.info("Userinfo decrypted: {}, {}", username, password);

        // 用户信息RSA私钥解密，方法二：使用hutool中的工具类进行解密
        RSA rsa = new RSA(privateKey, null);
        String username = new String(rsa.decrypt(formUser.getUsername(), KeyType.PrivateKey));
        String password = new String(rsa.decrypt(formUser.getPassword(), KeyType.PrivateKey));
        log.info("Userinfo decrypted: {}, {}", username, password);

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
