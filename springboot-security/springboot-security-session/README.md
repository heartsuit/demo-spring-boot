### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。

分布式集群架构下的 `Session` 共享一般有以下几种实现方案：

1. Session 复制

集群中任一服务器上的 `Session` 发生变化（增删改），该节点会把这个 `Session` 的所有内容序列化，然后广播给所有其它节点，从而实现 `Session` 同步。

2. Session 粘滞

利用 `Ngnix` 负载均衡策略中的 `ip_hash` 机制，将某个 `ip` 的所有请求都定向到同一台服务器上，即定向流量分发，这个其实不存在Session共享，仅是实现了用户请求与某个服务的绑定。 

3. Session 持久化

将所有的 `Session` 集中存储，存储到数据库中，保证 `Session` 的持久化，但是我们知道，随着用户数据量（活跃）的增加，查询数据库开销也随之增加。

4. Session 共享

将所有的 `Session` 集中存储，可使用分布式缓存方案比如 `Redis` 来缓存 `Session` ，实现 `Session` 共享，查询效率高，同时可以横向扩展。

> 显然，上述四种方案实际中应用更多的是最后一种：基于分布式缓存（eg: Memcached, Redis）的Session共享，我们就今天演示这种方案。

新建一个 `SpringBoot` 项目，起名 `springboot-security-Session` ，核心依赖为 `Web` , `SpringSecurity` , `SpringSession` 及 `Redis` ：

* pom依赖

``` xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

* 配置文件

``` yml
server:
  port: 8000
spring:
  redis:
    host: 10.16.1.110
    port: 6379
    pool.max-idle: 8
    pool.min-idle: 0
    pool.max-active: 8
    pool.max-wait: -1
    password:
    timeout: 1000
```

* 资源接口

创建资源接口： 登录之后默认跳转 `/` ，展示当前服务的端口号。

``` java
@RestController
public class SessionController {
  @Value("${server.port}")
  Integer port;

  @GetMapping(value = "/")
  public String greeting() {
    return String.valueOf(port);
  }
}
```

### 实验0：伪分布式集群Session共享

开启两个服务：

1. 一个运行在8000端口：http://localhost:8000
2. 一个运行在9000端口：http://localhost:9000

Note：在Idea中，可通过以下配置可同时运行一个服务的多个实例。

![2021-01-14-ParallelRun.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-14-ParallelRun.png)

实验步骤：

1. 在浏览器中先访问 `http://localhost:8000` ，这时要求登录，输入用户名：user，密码在控制台生成；登录成功后显示： `8000` ；
2. 然后继续在当前浏览器新开一个Tab，访问 `http://localhost:9000` ，不出意外，会直接显示（无需再次登录）： `9000` ；
3. 这样，便通过一个依赖 `spring-session-data-redis` 实现了 `Session` 共享（就不贴图了）；

Note：由于仍然采用的传统的 `Cookie-Session` 模式，所以上述实验必须在同一浏览器下进行，在请求时，浏览器会自动带上 `Cookie` （其中存了SessionID）；

#### 实验1：再次验证Session共享

在 `Controller` 中新增两个接口：一个写入键值对，一个读取键值，接口如下：

``` java
@RestController
public class SessionController {
  @Value("${server.port}")
  Integer port;

  @GetMapping(value = "/")
  public String greeting() {
    return String.valueOf(port);
  }

  @GetMapping(value = "/session/set")
  public String setSession(HttpSession session) {
    session.setAttribute("key", "value");
    return port + ": Session updated.";
  }

  @GetMapping(value = "/session/get")
  public String getSession(HttpSession session) {
    Object value = session.getAttribute("key");
    return port + "：" + (String) value;
  }
}
```

实验步骤：

1. 在浏览器中先访问 `http://localhost:8000/session/set` ，这时显示： `8000: Session updated.` ；
2. 然后继续在当前浏览器新开一个Tab，访问 `http://localhost:9000/session/get` ，不出意外，会显示： `9000：value` ；
3. 这样，实现了自定义键值对的 `Session` 共享；

### Source Code

[Github：https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-session](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-session)

### Reference

* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***