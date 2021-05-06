package com.heartsuit.tenant.controller;

import com.heartsuit.tenant.entity.User;
import com.heartsuit.tenant.mapper.UserMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloController {
    private final UserMapper userMapper;

    public HelloController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

//    @GetMapping("/insert")
//    private String insert() {
//        User user = new User();
//        user.setName("ok");
//        userMapper.insert(user);
//        System.out.println("Saved");
//        return "Saved";
//    }
}