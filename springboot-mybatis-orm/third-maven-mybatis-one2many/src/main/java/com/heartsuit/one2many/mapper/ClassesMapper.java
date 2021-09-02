package com.heartsuit.one2many.mapper;


import com.heartsuit.one2many.model.Classes;

public interface ClassesMapper {

    /*
     * 根据 id 查询班级 Classes 和它的学生
     * @param id
     * @return
     * @throws Exception
     */
    public Classes selectClassAndStudentsById(Integer id) throws Exception;

}