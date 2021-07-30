
## TDengine Spring JDBC Template Demo

`Spring JDBC Template` 简化了原生 JDBC Connection 获取释放等操作，使得操作数据库更加方便。

### 配置

修改 `src/main/resources/applicationContext.xml` 文件中 TDengine 的配置信息：

```xml
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.taosdata.jdbc.TSDBDriver"></property>
    <property name="url" value="jdbc:TAOS://127.0.0.1:6030/log"></property>
    <property name="username" value="root"></property>
    <property name="password" value="taosdata"></property>
</bean>

<bean id = "jdbcTemplate"  class="org.springframework.jdbc.core.JdbcTemplate" >
    <property name="dataSource" ref = "dataSource" ></property>
</bean>
```

### 打包运行

进入 `TDengine/tests/examples/JDBC/SpringJdbcTemplate` 目录下，执行以下命令可以生成可执行 jar 包。
```shell
mvn clean package
```
打包成功之后，进入 `target/` 目录下，执行以下命令就可运行测试：
```shell
java -jar SpringJdbcTemplate-1.0-SNAPSHOT-jar-with-dependencies.jar 
```

---
2021-7-21 17:39:12

1. 属性类型与表中字段类型不一致
实体类是这样的：

```java
public class Weather {
    private Timestamp ts;
    private float temperature;
    private int humidity;
}
```

可是代码中建表却是这样的：

executor.doExecute("create table if not exists jdbc_template.weather (ts timestamp, temperature int, humidity float)");


导致在表里看到是这样的：

```bash
taos> select * from weather;
           ts            | temperature |       humidity       |
===============================================================
 2021-07-21 17:36:43.142 |           8 |             60.00000 |
Query OK, 1 row(s) in set (0.001632s)

taos> describe weather;
             Field              |         Type         |   Length    |   Note   |
=================================================================================
 ts                             | TIMESTAMP            |           8 |          |
 temperature                    | INT                  |           4 |          |
 humidity                       | FLOAT                |           4 |          |
Query OK, 3 row(s) in set (0.000144s)
```

程序输出却是这样的：

Weather{ts=2021-07-21 17:36:43.142, temperature=8.0, humidity=60}

Note：temperature、humidity类型混乱。。

2. 测试方法时而通过，时而报错。。

com.taosdata.example.jdbcTemplate.BatcherInsertTest.batchInsert