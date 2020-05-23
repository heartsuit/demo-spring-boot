package com.heartsuit.springbootmybatis.oa.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2020-03-23
 */
@Data
public class Employee {
    private Long id;
    private String name;
    private Integer age;
    private String phone;
    private LocalDateTime createTime;
}
