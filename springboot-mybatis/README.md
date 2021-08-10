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

## 使用PageHelper实现分页查询

0. 添加依赖
```xml
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.5</version>
    </dependency>

```

1. 定义类，完成查询

PageRequest，PageResult，PageUtils

Dao层正常写SQL，Service层进行封装，PageHelper会进行拦截。

2. 前端传参

```bash
curl --location --request POST 'http://localhost:8000/employee/findByPage' \
--header 'Content-Type: application/json' \
--data-raw '{
    "page": 2,
    "size": 3
}'
```

3. 后端SQL

```sql
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@fc2dd3d] was not registered for synchronization because synchronization is not active
Cache Hit Ratio [SQL_CACHE]: 0.0
JDBC Connection [com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@7deb3d52] will not be managed by Spring
==>  Preparing: SELECT count(0) FROM demo_employee 
==> Parameters: 
<==    Columns: count(0)
<==        Row: 12000
<==      Total: 1
==>  Preparing: select id, name, age, phone, create_time from demo_employee LIMIT ?, ? 
==> Parameters: 3(Integer), 3(Integer)
<==    Columns: id, name, age, phone, create_time
<==        Row: 1240569421151879169, 李兰娟, 63, 15521344568, 2020-03-19 17:22:53
<==        Row: 1240569421151879172, 阿拉斯加, 34, 12345678901, 2021-08-09 12:24:32
<==        Row: 1240569421151879173, 5256d, 84, 18295551174, 2021-08-10 11:31:40
<==      Total: 3
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@fc2dd3d]
```

4. 接口响应
```json
{
    "currentPage": 2,
    "pageSize": 3,
    "totalPage": 4000,
    "totalCount": 12000,
    "records": [
        {
            "id": 1240569421151879169,
            "name": "李兰娟",
            "age": 63,
            "phone": "15521344568",
            "createTime": "2020-03-19T17:22:53"
        },
        {
            "id": 1240569421151879172,
            "name": "阿拉斯加",
            "age": 34,
            "phone": "12345678901",
            "createTime": "2021-08-09T12:24:32"
        },
        {
            "id": 1240569421151879173,
            "name": "5256d",
            "age": 84,
            "phone": "18295551174",
            "createTime": "2021-08-10T11:31:40"
        }
    ]
}
```