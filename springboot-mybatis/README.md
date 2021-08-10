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

POST http://localhost:8000/employee/export-xls

