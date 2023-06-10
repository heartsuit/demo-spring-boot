## 背景

上一篇文章[CentOS6.10上离线安装ClickHouse19.9.5.36并修改默认数据存储目录](https://blog.csdn.net/u013810234/article/details/131139730)记录了在旧版的操作系统上直接安装低版本 `ClickHouse` （脱胎于俄罗斯头号搜索引擎的技术）的过程，开启远程访问并配置密码；

其实通过 `Docker` 运行 `ClickHouse` 是我在2022年10月左右在虚拟机上实验的，当时 `DockerHub` 还可以打开，现在需要更换国内镜像才可以。。这里记录下当时使用 `Docker` 运行 `ClickHouse` 的过程。

![2023-06-10-ClickHouse.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-06-10-ClickHouse.jpg)

`Docker` 镜像仓库 `DockerHub` 地址（正常情况下，已无法访问。。可参考文末链接自行配置）： `https://hub.docker.com/r/clickhouse/clickhouse-server`

## 系统环境

```bash
[root@clickhouse1 local]# uname -a
Linux clickhouse1 3.10.0-1127.el7.x86_64 #1 SMP Tue Mar 31 23:36:51 UTC 2020 x86_64 x86_64 x86_64 GNU/Linux
[root@clickhouse1 local]# cat /proc/version
Linux version 3.10.0-1127.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-39) (GCC) ) #1 SMP Tue Mar 31 23:36:51 UTC 2020
[root@clickhouse1 local]# cat /etc/redhat-release
CentOS Linux release 7.8.2003 (Core)
```

## 启动运行

根据容器镜像文档中的说明，一键启动 `ClickHouse` 服务。

```bash
docker run -d \
	-p 8123:8123 -p9000:9000 -p9009:9009 --privileged \
	-v /opt/clickhouse/data:/var/lib/clickhouse/ \
	-v /opt/clickhouse/logs:/var/log/clickhouse-server/ \
	--name heartsuit-clickhouse-server --ulimit nofile=262144:262144 clickhouse/clickhouse-server:22.6.9.11

# 容器成功运行
[root@clickhouse1 ~]# docker ps
CONTAINER ID   IMAGE                                    COMMAND            CREATED              STATUS              PORTS                                                                                                                             NAMES
f1474927f130   clickhouse/clickhouse-server:22.6.9.11   "/entrypoint.sh"   About a minute ago   Up About a minute   0.0.0.0:8123->8123/tcp, :::8123->8123/tcp, 0.0.0.0:9000->9000/tcp, :::9000->9000/tcp, 0.0.0.0:9009->9009/tcp, :::9009->9009/tcp   heartsuit-clickhouse-server

# 验证服务OK
[root@clickhouse1 ~]# curl 192.168.44.148:8123
Ok.
```

用于管理 `Docker` 和防火墙的命令。

```bash
systemctl start docker
systemctl status docker

systemctl stop firewalld
systemctl disable firewalld
```

## 初步体验

这个版本的 `ClickHouse` 有个 `PlayGround` 端点： `http://IP:8123/play` ，可以直接通过 `Web` 的方式与 `OLAP` 数据库进行交互查询；

浏览器访问： `http://192.168.44.148:8123/play` ，快速体验下，建库、建表、查询等操作用起来跟 `MySQL` 差不多。

```sql
show databases;

CREATE DATABASE IF NOT EXISTS helloworld;

show databases;

CREATE TABLE helloworld.my_first_table
(
    user_id UInt32,
    message String,
    timestamp DateTime,
    metric Float32
)
ENGINE = MergeTree()
PRIMARY KEY (user_id, timestamp);

INSERT INTO helloworld.my_first_table (user_id, message, timestamp, metric) VALUES
    (101, 'Hello, ClickHouse!',                                 now(),       -1.0    ),
    (102, 'Insert a lot of rows per batch',                     yesterday(), 1.41421 ),
    (102, 'Sort your data based on your commonly-used queries', today(),     2.718   ),
    (101, 'Granules are the smallest chunks of data read',      now() + 5,   3.14159 )

SELECT * FROM helloworld.my_first_table;

SELECT * FROM helloworld.my_first_table ORDER BY timestamp;

SELECT * FROM helloworld.my_first_table ORDER BY timestamp FORMAT TabSeparated;
```

## 通过CSV写入数据表

```bash
# 新建data.csv，写入逗号分隔的以下内容
vi data.csv

102,This is data in a file,2022-02-22 10:43:28,123.45
101,It is comma-separated,2022-02-23 00:00:00,456.78
103,Use FORMAT to specify the format,2022-02-21 10:43:30,678.90

# 向docker容器内传文件
docker cp data.csv heartsuit-clickhouse-server:/

docker ps

# 进入容器内部
docker exec -it heartsuit-clickhouse-server /bin/bash

# 指定目录启动ClickHouse命令行客户端，导入csv文件
clickhouse-client \
> --query='INSERT INTO helloworld.my_first_table FORMAT CSV' < data.csv

# 验证导入结果
SELECT * FROM helloworld.my_first_table
```

Note：默认情况下，用户名默认为：default，密码为空；实际生产环境下如果需要开启远程访问，建议配置密码，可参考：[CentOS6.10上离线安装ClickHouse19.9.5.36并修改默认数据存储目录](https://blog.csdn.net/u013810234/article/details/131139730)

## 可能遇到的问题

如果关闭防火墙后没有重启 `Docker` ，运行容器时就会遇到下面的错误信息。

> Error response from daemon: Failed to Setup IP tables: Unable to enable SKIP DNAT rule:  (iptables failed: iptables --wait -t nat -I DOCKER -i br-af6aa0eafdec -j RETURN: iptables: No chain/target/match by that name.

## SpringBoot集成ClickHouse与MyBatisPlus

新建 `SpringBoot` 项目，常规操作，在依赖中选择 `Web` , `Lombok` , 附加 `MyBatis Plus` 。

### 核心依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--ClickHouse 依赖-->
        <dependency>
            <groupId>ru.yandex.clickhouse</groupId>
            <artifactId>clickhouse-jdbc</artifactId>
            <version>0.1.53</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
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
    driver-class-name: ru.yandex.clickhouse.ClickHouseDriver
    url: jdbc:clickhouse://IP:8123/poetry
    username: default
    password: CK666%
```

### 核心代码

通过集成 `MyBatis Plus` ， `Service` 、 `Mapper` 分别继承 `IService` 与 `BaseMapper` ，不贴代码了，具体见文末 `GitHub` 源码。

* 实体类

源自诗词数据库的31万多首诗词。表 `poetry` 结构如下，数据量：311828。

```sql
CREATE TABLE `poetry` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(150) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`yunlv_rule` TEXT NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`author_id` INT(10) UNSIGNED NOT NULL,
	`content` TEXT NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`dynasty` VARCHAR(10) NOT NULL COMMENT '诗所属朝代（S-宋代, T-唐代）' COLLATE 'utf8mb4_unicode_ci',
	`author` VARCHAR(150) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_unicode_ci'
ENGINE=InnoDB
AUTO_INCREMENT=311829;
```

```java
@Data
public class Poetry {
    @TableId
    private Integer id;
    private Integer authorId;
    private String title;
    private String content;
    private String yunlvRule;
    private String author;
    private char dynasty;
}
```

* 控制器

分别写了接口测试 `ClickHouse` 数据库的：列表查询、条件查询、分页查询、新增、修改、删除功能（这里在测试修改、删除功能时遇到了问题：与 `MyBatis Plus` 中通用的修改、删除语句不同，最后通过扩展 `MyBatis Plus` 源码实现了根据实体 `ID` 修改和删除的功能）。

![2023-06-10-RestAPI.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-06-10-RestAPI.jpg)

```java
@RestController
public class PoetryController {
    @Autowired
    private PoetryService poetryService;

    @Autowired
    private PoetryMapper poetryMapper; // 调用扩展方法

    @GetMapping("list")
    private List<Poetry> list() {
        return poetryService.list(new QueryWrapper<Poetry>().last("limit 10"));
    }

    @GetMapping("condition")
    private List<Poetry> listByCondition() {
        LambdaQueryWrapper<Poetry> wrapper = new QueryWrapper<Poetry>().lambda().eq(Poetry::getAuthor, "顾城");
        return poetryService.list(wrapper);
    }

    @GetMapping("page")
    private IPage<Poetry> listByPage(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "2") Integer size) {
        return poetryMapper.selectPage(new Page<>(page, size), null);
    }

    @PostMapping("save")
    public boolean save() {
        Poetry poetry = new Poetry();
        poetry.setId(400000); // 如果ClickHouse中没有设置ID自增，需要显式赋值
        poetry.setAuthorId(20000);
        poetry.setTitle("一代人");
        poetry.setContent("黑夜给了我黑色的眼睛，我却用它寻找光明");
        poetry.setYunlvRule("balabala");
        poetry.setDynasty('Z');
        poetry.setAuthor("顾城");

        return poetryService.save(poetry);
    }

    // Update和Delete语句在ClickHouse中报错，ClickHouse的修改和删除SQL操作与MySQL不同。
    // 参考解决：https://github.com/saimen90/clickhouse
    @PutMapping("update/{id}")
    public boolean update(@PathVariable Integer id) {
        Poetry poetry = poetryService.getById(id);
        poetry.setYunlvRule("wow");
        return poetryMapper.updateByIdClickHouse(poetry); // 扩展方法
    }

//    报错！！需要扩展MyBatis源码
//    @PutMapping("update")
//    public boolean updateByCondition() {
//        UpdateWrapper<Poetry> updateWrapper = new UpdateWrapper<>();
//        return poetryService.update(updateWrapper.lambda().set(Poetry::getDynasty, "C").eq(Poetry::getId, 40000));
//    }

    @DeleteMapping("delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        // 删除成功或失败，count都为0。。
        int count = poetryMapper.deleteByIdClickHouse(id); // 扩展方法
        return count > 0;
    }
}
```

### 扩展MyBatisPlus源码

核心代码在 `com/heartsuit/infrastructure` 路径下，主要参考了[https://github.com/saimen90/clickhouse](https://github.com/saimen90/clickhouse)。

![2023-06-10-ExtendMyBatisPlus.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2023-06-10-ExtendMyBatisPlus.jpg)

此外，新建 `SupperMapper.java` 接口，然后让实体的 `Mapper` 接口继承 `SuperMapper` 。

```java
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * @return
     * @Description: 删除并填充删除人信息
     * @param: id 主键id
     * @auther: zpq
     * @date: 2020/11/10 11:47 上午
     */
    boolean updateByIdClickHouse(@Param("et") T entity);

    /**
     * @return
     * @Description: 删除并填充删除人信息
     * @param: id 主键id
     * @auther: zpq
     * @date: 2020/11/10 11:47 上午
     */
    boolean updateClickHouse(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);

    /**
     * 主键删除
     *
     * @param id
     * @return
     */
    int deleteByIdClickHouse(Serializable id);
}
```

## Source Code

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-mybatisplus-clickhouse)

## Reference

* [https://github.com/saimen90/clickhouse](https://github.com/saimen90/clickhouse)
* [docker镜像仓库hub.docker.com无法访问](https://blog.waluna.top/2023/05/23/1374/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***
