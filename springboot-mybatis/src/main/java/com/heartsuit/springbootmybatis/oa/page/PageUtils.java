package com.heartsuit.springbootmybatis.oa.page;


import com.github.pagehelper.PageInfo;

/**
 * @Author Heartsuit
 * @Date 2021-08-10
 */
public class PageUtils {
    public static PageResult getPageResult(PageInfo<?> pageInfo) {
        PageResult pageResult = new PageResult();
        pageResult.setCurrentPage(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotalCount(pageInfo.getTotal());
        pageResult.setTotalPage(pageInfo.getPages());
        pageResult.setRecords(pageInfo.getList());
        return pageResult;
    }
}
