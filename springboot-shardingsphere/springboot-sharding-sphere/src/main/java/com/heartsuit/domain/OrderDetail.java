package com.heartsuit.domain;

import lombok.Data;

/**
 * @Author Heartsuit
 * @Date 2020-09-26
 */
@Data
public class OrderDetail {
    private Integer id;
    private String detail;
    private Integer orderId;
}
