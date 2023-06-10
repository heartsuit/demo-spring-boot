package com.heartsuit.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author Heartsuit
 * @Date 2023-06-09
 */
@Data
public class Poetry {
    @TableId
    private Integer id;
    private Integer authorId;
    private String title;
    private String content;
    private String yunlvRule;
    private String author;
    private char dynasty;
}
