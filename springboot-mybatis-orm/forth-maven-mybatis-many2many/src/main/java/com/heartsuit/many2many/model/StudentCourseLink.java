package com.heartsuit.many2many.model;

import lombok.Data;

import java.util.Date;

@Data
public class StudentCourseLink {
    private Student student;
    private Course course;
    private Date date;
}