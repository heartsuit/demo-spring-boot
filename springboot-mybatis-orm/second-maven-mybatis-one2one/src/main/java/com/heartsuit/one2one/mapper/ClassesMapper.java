package com.heartsuit.one2one.mapper;


import com.heartsuit.one2one.model.Classes;

public interface ClassesMapper {

    /*
     * 根据 id 查询班级 Classes
     * @param id
     * @return
     * @throws Exception
     */
    Classes selectClassById(Integer id) throws Exception;

}