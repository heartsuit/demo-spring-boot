package com.heartsuit.one2many.model;

import lombok.Data;

import java.util.List;

@Data
public class Classes {
    private Integer id;
    private String name;
    // One2Many
    private List<Student> students;
}