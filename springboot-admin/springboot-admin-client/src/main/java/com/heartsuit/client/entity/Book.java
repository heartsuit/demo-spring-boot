package com.heartsuit.client.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 书籍
 * </p>
 *
 * @author Heartsuit
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 阅读日期
     */
    private LocalDate readDate;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 译者
     */
    private String translators;

    /**
     * 出版日期
     */
    private LocalDate publishDate;

    /**
     * 装订
     */
    private String binding;

    /**
     * 页数
     */
    private String pages;

    /**
     * 封面图片
     */
    private String image;

    /**
     * 概述
     */
    private String summary;

    /**
     * 出版
     */
    private String publisher;

    /**
     * ISBN
     */
    private String isbn;

    /**
     * 价格
     */
    private String price;

    /**
     * 豆瓣链接
     */
    private String douban;

    /**
     * 评分
     */
    private String rating;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
