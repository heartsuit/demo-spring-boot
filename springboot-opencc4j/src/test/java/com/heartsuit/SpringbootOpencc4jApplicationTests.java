package com.heartsuit;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootOpencc4jApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void toSimple(){
        String original = "李白乘舟將欲行，忽聞岸上踏歌聲。|桃花潭水深千尺，不及汪倫送我情。";
        String result = ZhConverterUtil.toSimple(original);
        System.out.println(result);
        Assertions.assertEquals("李白乘舟将欲行，忽闻岸上踏歌声。|桃花潭水深千尺，不及汪伦送我情。", result);
    }
    @Test
    void toTraditional(){
        String original = "李白乘舟将欲行，忽闻岸上踏歌声。|桃花潭水深千尺，不及汪伦送我情。";
        String result = ZhConverterUtil.toTraditional(original);
        System.out.println(result);
        Assertions.assertEquals("李白乘舟將欲行，忽聞岸上踏歌聲。|桃花潭水深千尺，不及汪倫送我情。", result);
    }
}
