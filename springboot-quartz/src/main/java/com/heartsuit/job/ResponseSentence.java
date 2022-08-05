package com.heartsuit.job;

import lombok.Data;

import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-11-12
 */
@Data
public class ResponseSentence {
    private int code;
    private String message;
    private Map<String, String> result;

}
