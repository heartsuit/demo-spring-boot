package com.heartsuit.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @Author Heartsuit
 * @Date 2020-06-12
 */
@Data
//@Document(indexName = "book", useServerConfiguration = true, createIndex = false)
@Document(indexName = "book")
public class Book {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String author;

    @Field(name = "word_count", type = FieldType.Integer)
    private Integer wordCount;

    /**
     * 1. Jackson日期时间序列化问题：
     * Cannot deserialize value of type `java.time.LocalDateTime` from String "2020-06-04 15:07:54": Failed to deserialize java.time.LocalDateTime: (java.time.format.DateTimeParseException) Text '2020-06-04 15:07:54' could not be parsed at index 10
     * 解决：@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     * 2. 日期在ES存为long类型
     * 解决：需要加format = DateFormat.custom
     * 3. java.time.DateTimeException: Unable to obtain LocalDate from TemporalAccessor: {DayOfMonth=5, YearOfEra=2020, MonthOfYear=6},ISO of type java.time.format.Parsed
     * 解决：pattern = "uuuu-MM-dd HH:mm:ss" 即将yyyy改为uuuu，或8uuuu: pattern = "8uuuu-MM-dd HH:mm:ss"
     * 参考：https://www.elastic.co/guide/en/elasticsearch/reference/current/migrate-to-java-time.html#java-time-migration-incompatible-date-formats
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(name = "publish_date", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime publishDate;
}
