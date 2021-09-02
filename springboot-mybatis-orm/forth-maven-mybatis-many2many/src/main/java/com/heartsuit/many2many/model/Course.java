package com.heartsuit.many2many.model;

import lombok.Data;

import java.util.List;

@Data
public class Course {
    private Integer id;
    private String name;
    private Integer credit;
    private List<Student> students;
}