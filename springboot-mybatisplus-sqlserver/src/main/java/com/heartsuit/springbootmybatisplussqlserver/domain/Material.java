package com.heartsuit.springbootmybatisplussqlserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@Data
@TableName("JCZH_GBGL_AT0912.dbo.T_WLFlow")
public class Material {
    private Integer cangkuNum;
    private String wuliaoName;
    private Integer quantity;
    private Date addTime;
}
