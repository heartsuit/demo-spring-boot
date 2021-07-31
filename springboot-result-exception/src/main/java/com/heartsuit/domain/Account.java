package com.heartsuit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Heartsuit
 * @Date 2021-07-31
 */
@Data
@AllArgsConstructor
public class Account {
    private Integer id;
    private String accountCode;
    private String accountName;
    private BigDecimal amount;
}
