package com.heartsuit.one2one.model;

import lombok.Data;

@Data
public class Classes {
    private Integer id;
    private String name;
    // One2One
    private HeadTeacher teacher;
}