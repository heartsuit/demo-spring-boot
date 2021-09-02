package com.heartsuit.mybatis.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2021-09-02
 */
@Data
public class Employee {
    private Long id;
    private String name;
    private Integer age;
    private String phone;
    private LocalDateTime createTime;
}
