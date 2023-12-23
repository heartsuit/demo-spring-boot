## 背景

繁体中文转为简体中文的需求非常常见，特别是在中文语境下的文本处理和翻译应用中。有很多现成的工具和库可以实现这个功能，比如 `OpenCC` 、 `HanLP` 等。从网上下载的 `MySQL` 版诗词数据库中的诗词数据都是繁体字，这里使用 `SpringBoot` 集成调用第三方 `jar` 包 `opencc4j` ，完成数据表中标题、作者与内容等字段的繁体中文到简体中文的转换。

繁体中文转为简体中文有几个意义：
1. 便于阅读和理解：简体中文更简洁，对于一些非中国大陆地区的人来说更易于理解。
2. 标准化：在一些场合，如国际交流、官方文件等，使用简体中文可以提高统一性和标准化。
3. 数字输入：在数字输入和计算机处理方面，简体中文更易于处理和识别。

总的来说，繁体中文转为简体中文可以提高交流和理解的效率，也有助于标准化和数字化处理。

## 引入依赖

```xml
        <!-- Opencc4j 支持中文繁简体转换 -->
        <dependency>
            <groupId>com.github.houbb</groupId>
            <artifactId>opencc4j</artifactId>
            <version>1.8.1</version>
        </dependency>
```

## 编写测试类

### 繁体中文转为简体中文

引入依赖 `import com.github.houbb.opencc4j.util.ZhConverterUtil;` 后，直接调用 `ZhConverterUtil` 工具类的 `toSimple` 方法，可将繁体中文转换为简体中文。

```java
    @Test
    void toSimple(){
        String original = "李白乘舟將欲行，忽聞岸上踏歌聲。|桃花潭水深千尺，不及汪倫送我情。";
        String result = ZhConverterUtil.toSimple(original);
        System.out.println(result);
        Assertions.assertEquals("李白乘舟将欲行，忽闻岸上踏歌声。|桃花潭水深千尺，不及汪伦送我情。", result);
    }
```

![2023-12-23-toSimple.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-12-23-toSimple.jpg)

### 简体中文转为繁体中文

调用 `ZhConverterUtil` 工具类的 `toTraditional` 方法，可将简体中文转换为繁体中文。

```java
    @Test
    void toTraditional(){
        String original = "李白乘舟将欲行，忽闻岸上踏歌声。|桃花潭水深千尺，不及汪伦送我情。";
        String result = ZhConverterUtil.toTraditional(original);
        System.out.println(result);
        Assertions.assertEquals("李白乘舟將欲行，忽聞岸上踏歌聲。|桃花潭水深千尺，不及汪倫送我情。", result);
    }
```

![2023-12-23-toTraditional.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-12-23-toTraditional.jpg)

完整代码如下：

```java
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
```

## 诗词数据库繁体中文转为简体中文

关于将数据表中标题、作者与内容等字段的繁体中文到简体中文的转换，具体可参考[基于ETLCloud的自定义规则调用第三方jar包实现繁体中文转为简体中文](https://blog.csdn.net/u013810234/article/details/132574809?spm=1001.2014.3001.5501)，其中采用大数据的集成工具实现了在数据迁移过程中的转换。

## 小总结

上述内容主要介绍了如何使用 `SpringBoot` 集成调用第三方 `jar` 包 `opencc4j` 实现繁体中文到简体中文的转换。文章中提到了引入 `opencc4j` 依赖，编写了测试类来进行繁简体中文转换的示例，以及在诗词数据库中进行繁简体中文转换的应用。
