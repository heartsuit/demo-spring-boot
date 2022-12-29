## 背景

通过 `SpringBoot` 与 `MyBatis Plus` 实现与 `SQLServer` 的集成， `CRUD` 。

## SpringBoot集成SQLServer

新建 `SpringBoot` 项目，常规操作，在依赖中选择 `Web` , `Lombok` , `SQLServer` ，附加 `MyBatis Plus` 。

### 核心依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/net.sourceforge.jtds/jtds -->
        <dependency>
            <groupId>net.sourceforge.jtds</groupId>
            <artifactId>jtds</artifactId>
            <version>1.3.1</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

Note：

1. The server selected protocol version TLS10 is not accepted by client preferences [TLS12]

一开始是用的 `SpringBoot` 自带的 `SQLServer` 依赖，然后就报了上述错误。

```xml
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <scope>runtime</scope>
        </dependency>
```

查了下说是要修改 `JDK` 安全相关的配置项，放弃。。改用以下依赖实现。

```xml
        <!-- https://mvnrepository.com/artifact/net.sourceforge.jtds/jtds -->
        <dependency>
            <groupId>net.sourceforge.jtds</groupId>
            <artifactId>jtds</artifactId>
            <version>1.3.1</version>
        </dependency>
```

2. 	java.lang.AbstractMethodError: null

原因： `net.sourceforge.jtds.jdbc.JtdsConnection` 没有实现 `isValid` ，因此需要指定 `connection-test-query` 以确保 `isValid` 不调用该方法。

解决方法：在 `application.yml` 或 `application.properties` 配置文件中添加以下内容。

```properties
spring.datasource.hikari.connection-test-query=SELECT 1
```

### 配置文件

```yaml
mybatis-plus:
  configuration:
    # 开启下划线转驼峰
    map-underscore-to-camel-case: true
    # 指定默认枚举类型的类型转换器
    default-enum-type-handler: com.baomidou.mybatisplus.extension.handlers.MybatisEnumTypeHandler
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    banner: false
    db-config:
      # 逻辑删除（软删除）
      logic-delete-value: NOW()
      logic-not-delete-value: 'NULL'
  mapper-locations: classpath:mapper/*.xml

spring:
  datasource:
    driver-class-name: net.sourceforge.jtds.jdbc.Driver
    url: jdbc:jtds:sqlserver://192.168.23.30:1433;database=JCZH_GBGL_AT0912
    username: sa
    password: xq122911
    hikari:
      connection-test-query: SELECT 1
```

### 核心代码

通过集成 `MyBatis Plus` ， `Service` 、 `Mapper` 分别继承 `IService` 与 `BaseMapper` ，不贴代码了，具体见文末 `GitHub` 源码。数据表使用一个测算表，实际根据需要修改。

* 实体

```java
@Data
@TableName("JCZH_GBGL_AT0912.dbo.T_WLFlow")
public class Material {
    private Integer cangkuNum;
    private String wuliaoName;
    private Integer quantity;
    private Date addTime;
}
```

* 控制器

分别写了接口测试 `SQLServer` 数据库的：列表查询、新增、修改、删除、事务功能。

![2022-12-31-SQLServer.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-12-31-SQLServer.jpg)

```java
@RestController
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("list")
    public List<Material> list(){
        return materialService.list();
    }

    @PostMapping("save")
    public boolean save(){
        Material material = new Material();
        material.setCangkuNum(888);
        material.setQuantity(100);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());

        return materialService.save(material);
    }

    @PutMapping("update")
    public boolean update(){
        UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
        return materialService.update(updateWrapper.lambda().set(Material::getQuantity, 99).eq(Material::getCangkuNum, 888));
    }

    @DeleteMapping("delete/{id}")
    public boolean deleteByCondition(@PathVariable Integer id){
        return materialService.removeById(id);
    }

    @DeleteMapping("deleteByCondition")
    public boolean deleteByCondition(){
        return materialService.remove(new QueryWrapper<Material>().lambda().eq(Material::getCangkuNum, 888));
    }

    @PostMapping("saveTransaction")
    @Transactional
    public boolean saveWithTransaction(){
        Material material = new Material();
        material.setCangkuNum(777);
        material.setQuantity(77);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());
        materialService.save(material);

        // Exception
        int x = 1/0;

        material.setCangkuNum(999);
        material.setQuantity(99);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());

        return materialService.save(material);
    }
}
```

## Source Code

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-mybatisplus-sqlserver)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***
