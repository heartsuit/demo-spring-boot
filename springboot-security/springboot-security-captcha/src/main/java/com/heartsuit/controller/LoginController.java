package com.heartsuit.controller;

/**
 * @Author Heartsuit
 * @Date 2020-12-26
 */

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import vo.FormUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
public class LoginController {
    // 模拟数据库或缓存，存储验证码
    private Map<String, String> captchaStore = new ConcurrentHashMap<>();

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        // gif类型
//        GifCaptcha captcha = new GifCaptcha(130, 48);

        // 中文类型
//        ChineseCaptcha captcha = new ChineseCaptcha(130, 48);

        // 中文gif类型
//        ChineseGifCaptcha captcha = new ChineseGifCaptcha(130, 48);

        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);

        // 几位数运算(默认是两位)
        captcha.setLen(2);

        // 获取生成的文本
        String code = captcha.text().equals("0.0") ? "0" : captcha.text();
        // 验证码对应的UUID
        String uuid = UUID.randomUUID().toString();

        log.info("Captcha generated: {}, UUID generated:{}", code, uuid);

        // 保存生成的验证码信息
        captchaStore.put(uuid, code);

        // 返回验证码信息
        model.addAttribute("img", captcha.toBase64());
        model.addAttribute("uuid", uuid);
        return "login";
    }

    @PostMapping("/signin")
    @ResponseBody
    public String signin(FormUser formUser, HttpServletRequest request) throws Exception {
        log.info("formUser: {}", formUser);

        // 查询验证码
        String code = (String) captchaStore.get(formUser.getUuid());
        // 清除验证码
        captchaStore.remove(formUser.getUuid());

        // 核验验证码
        if (StringUtils.isEmptyOrWhitespace(code)) {
            throw new Exception("EXPIRED");
        }
        if (StringUtils.isEmptyOrWhitespace(formUser.getCode()) || !formUser.getCode().equalsIgnoreCase(code)) {
            throw new Exception("INVALID");
        }

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(formUser.getUsername(), formUser.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    @RequestMapping(value = "/")
    @ResponseBody
    public String loginSuccess() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return SecurityContextHolder.getContext().getAuthentication().toString();
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
}
