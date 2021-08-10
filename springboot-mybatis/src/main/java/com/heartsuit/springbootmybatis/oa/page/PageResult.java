package com.heartsuit.springbootmybatis.oa.page;

import lombok.Data;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-08-10
 */
@Data
public class PageResult {
    //当前页数
    private int currentPage;
    //每页记录数
    private int pageSize;

    //总页数
    private int totalPage;
    //总记录数
    private long totalCount;

    //列表数据
    private List<?> records;
}
