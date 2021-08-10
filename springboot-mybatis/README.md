## SpringBoot集成MyBatis基础的CRUD

## 使用hutool工具导出数据为Excel

0. 依赖
```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.5.6</version>
</dependency>

<!--Export as Excel-->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>4.1.1</version>
</dependency>
```
1. 测试接口：全量导出

GET http://localhost:8000/employee/export-xls

Note: GET方式，方便测试，实际建议POST。

2. 测试1万条数据导出效率

- 批量向数据表插入数万条数据，再次测试导出效率；
- 其实，导出时间取决于查效率以及查出的总数据量（涉及写入Excel以及Excel传输两部分时间）；

从数万条记录中导出1万条数据，秒级。