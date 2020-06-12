package com.heartsuit.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2020-06-04
 */
@Data
public class Book {
    private String title;
    private String author;
    //    @JsonProperty("word_count")
    @JSONField(name = "word_count")
    private Integer wordCount;

//    @JsonProperty("publish_date")
    /**
     * Jackson日期时间序列化问题：
     * Cannot deserialize value of type `java.time.LocalDateTime` from String "2020-06-04 15:07:54": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2020-06-04 15:07:54' could not be parsed at index 10
     */
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(name = "publish_date", format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishDate;
}
