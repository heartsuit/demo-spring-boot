package com.heartsuit.domain;

import lombok.Data;

/**
 * @Author Heartsuit
 * @Date 2020-09-24
 */
@Data
public class Order {
    private Integer id;
    private Integer orderType;
    private Integer customerId;
    private Double amount;
}
