基于共享数据库，共享数据的方式实现多租户

参考：
- MybatisPlus官方的示例：https://github.com/baomidou/mybatis-plus-samples
- https://www.jianshu.com/p/742f40eb9937

除了一些系统共用的表以外，其他租户相关的表，我们都需要在sql语句中的加上AND t.tenant_id = ?查询条件，稍不注意就会导致数据越界；
MybatisPlus对这种多租户方式做了支持，可以极为方便的实现多租户SQL解析器，在需要限制租户处自动添加tenant_id的条件或者值。

采用内存型数据库h2，方便测试
访问链接：http://localhost:8080/h2-console
默认会在用户根目录下生成数据库文件，DBC URL为：jdbc:h2:~/test
采用内存型，连接时，修改JDBC URL为配置的数据库：jdbc:h2:mem:test

Note：有Controller中的
    @Autowired
    private UserMapper userMapper;
才会打印带有Original SQL的日志

SELECT:
Original SQL: SELECT  id,tenant_id,name  FROM user
parser sql: SELECT id, tenant_id, name FROM user WHERE user.tenant_id = 1
==>  Preparing: SELECT id, tenant_id, name FROM user WHERE user.tenant_id = 1 
==> Parameters: 
<==    Columns: ID, TENANT_ID, NAME
<==        Row: 1, 1, Jone
<==        Row: 2, 1, Jack
<==        Row: 3, 1, Tom
<==      Total: 3


INSERT: 
Original SQL: INSERT INTO user  ( id,

name )  VALUES  ( ?,

? )
parser sql: INSERT INTO user (id, name, tenant_id) VALUES (?, ?, 1)
==>  Preparing: INSERT INTO user (id, name, tenant_id) VALUES (?, ?, 1) 
==> Parameters: 1390225403008921601(Long), ok(String)
<==    Updates: 1

UPDATE: 
Original SQL: UPDATE user  SET name=?  WHERE id=?
parser sql: UPDATE user SET name = ? WHERE user.tenant_id = 1 AND id = ?
==>  Preparing: UPDATE user SET name = ? WHERE user.tenant_id = 1 AND id = ? 
==> Parameters: mp(String), 1(Long)
<==    Updates: 1

DELETE: 
Original SQL: DELETE FROM user WHERE id=?
parser sql: DELETE FROM user WHERE user.tenant_id = 1 AND id = ?
==>  Preparing: DELETE FROM user WHERE user.tenant_id = 1 AND id = ? 
==> Parameters: 3(Long)
<==    Updates: 1

COUNT: 可通过注解@SqlParser(filter = true)控制是否增加租户ID过滤
Original SQL: select count(1) from user
parser sql: SELECT count(1) FROM user WHERE user.tenant_id = 1
==>  Preparing: SELECT count(1) FROM user WHERE user.tenant_id = 1 
==> Parameters: 
<==    Columns: COUNT(*)
<==        Row: 3
<==      Total: 1

JOIN: 
Original SQL: select a.name as addr_name, u.id, u.name
        from user_addr a
        left join user u on u.id=a.user_id
         WHERE a.name like concat(concat('%',?),'%')
parser sql: SELECT a.name AS addr_name, u.id, u.name FROM user_addr a LEFT JOIN user u ON u.id = a.user_id AND u.tenant_id = 1 WHERE a.name LIKE concat(concat('%', ?), '%')
==>  Preparing: SELECT a.name AS addr_name, u.id, u.name FROM user_addr a LEFT JOIN user u ON u.id = a.user_id AND u.tenant_id = 1 WHERE a.name LIKE concat(concat('%', ?), '%') 
==> Parameters: add(String)
<==    Columns: ADDR_NAME, ID, NAME
<==        Row: addr1, 1, Jone
<==        Row: addr2, 1, Jone
<==      Total: 2


遇到的问题：

0. 由于新换了电脑，没有导入以前的配置，IDEA中，添加依赖spring-boot-devtools后，在修改了Java代码后，不能自动重启服务。
解决方法：
按快捷键Shift+Ctrl+Alt+/，选择第一项registry，找到compiler.automake.allow.when.app.running，勾选；
Ctrl+Alt+S打开设置，找到Build, Execution, Deployment -> Compiler，勾选Build project automatically即可。
之后，修改了Java代码后就可以自动重启服务了。（如果不希望服务重启需要在application.properties或application.yml中添加spring.devtools.reatart.enable=false）

1. 在UserMapper.xml中写的方法，报错：org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.heartsuit.tenant.mapper.UserMapper.myCount
解决方法：在pom中的build标签下添加：
```xml
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <filtering>false</filtering>
            <includes>
                <include>**/mapper/*.xml</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*</include>
            </includes>
        </resource>
    </resources>
```

2. @AutoWired或者构造方法注入Mapper相关的接口时，IDEA标红（其实没错误），提示信息：Could not autowire. No beans of 'UserMapper' type found.
解决方法：在对应的Mapper接口上，添加@Repository注解。 
