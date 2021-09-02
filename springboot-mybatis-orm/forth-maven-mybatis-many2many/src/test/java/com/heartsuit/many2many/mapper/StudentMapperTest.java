package com.heartsuit.many2many.mapper;

import com.heartsuit.many2many.model.Course;
import com.heartsuit.many2many.model.Student;
import com.heartsuit.many2many.model.StudentCourseLink;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-09-02
 */
public class StudentMapperTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Before
    public void initialize() {
        // Mybatis 配置文件
        String resource = "mybatis.cfg.xml";

        // 得到配置文件流
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 创建会话工厂，传入 MyBatis 的配置文件信息
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void selectStudentCourse() {
        // 通过工厂得到 SqlSession
        SqlSession session = sqlSessionFactory.openSession();

        StudentMapper mapper = session.getMapper(StudentMapper.class);
        try {
            List<Student> students = mapper.selectStudentCourse();
            session.commit();
            for (Student stu : students) {
                System.out.println(stu.getId() + "," + stu.getName() + "," + stu.getSex() + "," + stu.getAge() + ":");
                List<Course> courses = stu.getCourses();
                for (Course cou : courses) {
                    System.out.println(cou.getId() + "," + cou.getName() + "," + cou.getCredit());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        // 释放资源
        session.close();
    }

    @Test
    public void deleteStudentCourseById() {
        SqlSession session = sqlSessionFactory.openSession();

        StudentMapper mapper = session.getMapper(StudentMapper.class);
        try {
            Student student = new Student();
            student.setId(1);
            Course course = new Course();
            course.setId(2);
            StudentCourseLink scLink = new StudentCourseLink();
            scLink.setStudent(student);
            scLink.setCourse(course);
            mapper.deleteStudentCourseById(scLink);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        session.close();
    }
}
