package com.heartsuit.springbootmybatis.oa.page;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Heartsuit
 * @Date 2021-08-10
 */
@Data
public class PageRequest implements Serializable {
    private int page;
    private int size;
}
