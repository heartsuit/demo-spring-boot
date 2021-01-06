package com.heartsuit.mapper;

import com.heartsuit.domain.DictOderType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class DictOrderTypeMapperTest {

    @Autowired
    private DictOrderTypeMapper dictOrderTypeMapper;

    @Test
    void insertDictOrderType() {
        for (int i = 1; i <= 10; i++) {
            DictOderType dictOderType = new DictOderType();
            dictOderType.setOrderType(String.valueOf(i));

            dictOrderTypeMapper.insertDictOrderType(dictOderType);
            log.info(i + " written");
        }
    }
}