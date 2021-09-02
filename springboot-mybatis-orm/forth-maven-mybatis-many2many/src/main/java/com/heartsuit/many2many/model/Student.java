package com.heartsuit.many2many.model;

import lombok.Data;

import java.util.List;

@Data
public class Student {
    private Integer id;
    private String name;
    private String sex;
    private Integer age;
    private List<Course> courses;
}