package com.heartsuit.many2many.mapper;


import com.heartsuit.many2many.model.Student;
import com.heartsuit.many2many.model.StudentCourseLink;

import java.util.List;

public interface StudentMapper {
    /*
     * 查询所有学生及他们的选择课程的信息
     * @return
     * @throws Exception
     */
    public List<Student> selectStudentCourse() throws Exception;

    /*
     * 删除指定 id 用户的某门课（根据课程 id）的选课情况
     * @param StudentCourseLink
     * @throws Exception
     */
    public void deleteStudentCourseById(StudentCourseLink scLink) throws Exception;
}