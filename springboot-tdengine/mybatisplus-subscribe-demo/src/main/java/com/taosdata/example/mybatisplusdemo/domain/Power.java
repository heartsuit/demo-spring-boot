package com.taosdata.example.mybatisplusdemo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author Heartsuit
 * @Date 2021-07-16
 */
@Data
public class Power {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Timestamp ts;
    private Integer voltage;
    private Float currente;
    private Float temperature;
    private Integer sn;
    private String city;
    private Integer groupid;
}
