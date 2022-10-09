package com.heartsuit.controller;

/**
 * @Author Heartsuit
 * @Date 2022-10-09
 */

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
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
    @Value("${sm2.private_key}")
    private String privateKey;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     * 测试匿名访问时403
     * @return
     */
    @GetMapping("/hi")
    public String hi() {
        return "Hi~";
    }

    /**
     * 测试SM2公钥加密与私钥解密
     * @param formUser
     * @return
     */
    @PostMapping("/sm2")
    public String sm2(@RequestBody FormUser formUser) {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p\n" +
                "vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==";

        SM2 sm2 = new SM2(privateKey, publicKey);
        String encryptedString = sm2.encryptBcd(formUser.getUsername(), KeyType.PublicKey);
        String decryptedString = StrUtil.utf8Str(sm2.decryptFromBcd(encryptedString, KeyType.PrivateKey));

        log.info("密文：" + encryptedString);
        log.info("明文：" + decryptedString);

        return decryptedString;
    }

    /**
     * 认证接口，其中使用SM2国密算法进行私钥解密
     * @param formUser 加密后的用户信息
     * @param request
     * @return 认证后的用户
     */
    @PostMapping("/login")
    public String login(@RequestBody FormUser formUser, HttpServletRequest request) {
        log.info("formUser encrypted: {}", formUser);

        // 用户信息SM2私钥解密，使用hutool中的工具类进行解密
        SM2 sm2 = new SM2(privateKey, null);
        String username = StrUtil.utf8Str(sm2.decryptFromBcd(formUser.getUsername(), KeyType.PrivateKey));
        String password = StrUtil.utf8Str(sm2.decryptFromBcd(formUser.getPassword(), KeyType.PrivateKey));

        log.info("Userinfo decrypted: {}, {}", username, password);

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
