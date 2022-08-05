package com.heartsuit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * @Author Heartsuit
 * @Date 2022-01-26
 */
@RestController
@RequestMapping("mail")
public class MailController {
    @Value("${spring.mail.username}")
    private String mailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("text")
    public String sendTextMail(@RequestParam String target) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailSender);
        mailMessage.setTo(target);
        mailMessage.setSubject("XY平台注册验证码邮件");

        String code = getCode();
        mailMessage.setText("您的验证码为: " + code
                + "（有效期5分钟）。为了保证您的帐户安全，请勿向任何人提供此验证码。本邮件由系统自动发送，请勿直接回复。");

        javaMailSender.send(mailMessage);
        return code;
    }

    @PostMapping("html")
    public String sendHTMLMail(@RequestParam String target) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mailSender);
        mimeMessageHelper.setTo(target);
        mimeMessageHelper.setSubject("XY平台注册验证码邮件");

        String code = getCode();
        String sb = "<h2>您的验证码为：</h2>" +
                "<p style='color:red; text-align:center'> " + code + "（有效期5分钟）</p>" +
                "<p style='font-weight:bold; text-decoration:underline'>为了保证您的帐户安全，请勿向任何人提供此验证码。本邮件由系统自动发送，请勿直接回复。</p>";
        mimeMessageHelper.setText(sb, true); // 关键是第二个参数，开启将富文本渲染为HTML

        javaMailSender.send(mimeMessage);
        return code;
    }

    @PostMapping("attachment")
    public String sendAttachmentMail(@RequestParam String target) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mailSender);
        mimeMessageHelper.setTo(target);
        mimeMessageHelper.setSubject("XY平台注册验证码邮件");

        String code = getCode();
        mimeMessageHelper.setText("您的验证码为: " + code
                + "（有效期5分钟）。为了保证您的帐户安全，请勿向任何人提供此验证码。本邮件由系统自动发送，请勿直接回复。"); // 关键是第二个参数，开启将富文本渲染为HTML

        // 附件
        String fileName = "avatar.jpg";
        Resource resource = new ClassPathResource(fileName);
        mimeMessageHelper.addAttachment(fileName, resource.getFile());

        javaMailSender.send(mimeMessage);
        return code;
    }

    @PostMapping("inline")
    public String sendInlineMail(@RequestParam String target) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mailSender);
        mimeMessageHelper.setTo(target);
        mimeMessageHelper.setSubject("XY平台注册验证码邮件");

        String code = getCode();
        String resourceId = "avatar"; // 每个资源对应一个ID
        String sb = "<h2>您的验证码为：</h2>" +
                "<p style='color:red; text-align:center'> " + code + "（有效期5分钟）</p>" +
                "<img style='width:64px; height:64px;' src='cid:" + resourceId + "' />" +
                "<p style='font-weight:bold; text-decoration:underline'>为了保证您的帐户安全，请勿向任何人提供此验证码。本邮件由系统自动发送，请勿直接回复。</p>";
        mimeMessageHelper.setText(sb, true); // 关键是第二个参数，开启将富文本渲染为HTML

        // 内联静态资源
        String fileName = "avatar.jpg";
        Resource resource = new ClassPathResource(fileName);
        mimeMessageHelper.addInline(resourceId, resource.getFile());

        javaMailSender.send(mimeMessage);
        return code;
    }

    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping("template")
    public String sendTemplateMail(@RequestParam String target) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mailSender);
        mimeMessageHelper.setTo(target);
        mimeMessageHelper.setSubject("HTML模板邮件");

        // HTML 模板
        Context context = new Context();
        context.setVariable("username", "HeartSuit");
        context.setVariable("url", "https://avatars.githubusercontent.com/u/8071981?s=40&v=4");
        String content = templateEngine.process("notify", context);
        mimeMessageHelper.setText(content, true); // 关键是第二个参数，开启将富文本渲染为HTML

        javaMailSender.send(mimeMessage);
        return "OK";
    }

    @GetMapping("code")
    public String code() {
        return getCode();
    }

    public String getCode() {
        String str = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // no zero
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * str.length());
            code.append(str.charAt(index));
        }
        return code.toString();
    }
}
