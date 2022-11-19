package com.heartsuit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author Heartsuit
 * @Date 2022-11-15
 */
@Data
@TableName("EMP")
public class Employee {
    @TableId
    private Integer empno;

    private String ename;
    private String job;
    private Integer mgr;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date hiredate;

    private Integer sal;
    private Integer comm;
    private Integer deptno;
}
