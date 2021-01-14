### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验`SpringSecurity`的各项Feature。

### 实验0：`SpringSecurity`默认开启`CSRF`防护

现在我们在`springboot-security`项目的`HelloController.java`中新增一个`POST`接口：`/ok`。

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "hello springsecurity";
    }

    @PostMapping("/ok")
    public String ok(){
        return "ok";
    }
}
```

当然这个POST接口无法直接在浏览器中发起请求，我们需要借助PostMan来实现POST请求的发送。把浏览器中的Cookie复制到PostMan中。

- 先发GET /hello，正常

![2020-12-08-SpringSecurityGET.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-08-SpringSecurityGET.png)

- 再发POST /ok，403了。。

![2020-12-08-SpringSecurityPOST.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-08-SpringSecurityPOST.png)

那么，问题来了，两个请求都是在登录状态下进行的，为什么GET成功，POST返回403了？

其实`SpringSecurity`默认就开启了`CSRF`防护，这在上一篇及官网中关于SpringBoot自动配置项那里可以看到。并且`SpringSecurity`默认忽略"GET", "HEAD", "TRACE", "OPTIONS"等请求，源码如下：

```java
/**
 * Specify the {@link RequestMatcher} to use for determining when CSRF should be
 * applied. The default is to ignore GET, HEAD, TRACE, OPTIONS and process all other
 * requests.
 *
 * @param requireCsrfProtectionMatcher the {@link RequestMatcher} to use
 * @return the {@link CsrfConfigurer} for further customizations
 */
public CsrfConfigurer<H> requireCsrfProtectionMatcher(
        RequestMatcher requireCsrfProtectionMatcher) {
    Assert.notNull(requireCsrfProtectionMatcher,
            "requireCsrfProtectionMatcher cannot be null");
    this.requireCsrfProtectionMatcher = requireCsrfProtectionMatcher;
    return this;
}
```

```java
private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
    private final HashSet<String> allowedMethods = new HashSet<>(
            Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));
    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.web.util.matcher.RequestMatcher#matches(javax.
     * servlet.http.HttpServletRequest)
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        return !this.allowedMethods.contains(request.getMethod());
    }
}
```

### 实验1：CSRF GET攻击

CSRF: Cross-Site Request Forgery 跨站请求伪造。一些网站比如知乎、简书中的外部链接，点击之后会有提示（免责声明），此操作有风险是否继续，这便与CSRF密切相关。

![2020-12-08-SpringSecurityJump.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-08-SpringSecurityJump.png)

结合上篇文章，新建一个`SpringBoot`项目，起名`spring-security-csrf`，核心依赖为`Web`与`Thymeleaf`，模拟一个钓鱼网站。

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
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

建好项目后，创建一个简单的HelloController.java，包含一个`/`的`GET`请求，返回一个页面`index.html`:

- 后端接口
```java
@Controller
public class HelloController {
    @RequestMapping("/")
    public String hello(){
        return "index";
    }
}
```

- 前端模板

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
Fishing: <img src="http://hello:8080/hello">
</body>
</html>
```

Note：
- 以下步骤在Firefox浏览器完成；
- 修改`springboot-security`的后端接口，增加输出打印，方便后续确定请求是否进入后端；

```java
@RestController
@Slf4j
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        log.info("Hello ");
        return "hello";
    }

    @PostMapping("/ok")
    public String ok(){
        log.info("ok");
        return "ok";
    }
}
```

- 前提：为了模拟不同域名下的请求（即CSRF），我们在本地的`hosts`文件添加如下内容：

```
127.0.0.1 hello
127.0.0.1 world
```

实验步骤：
1. 启动两个项目：`springboot-security`在8080端口、`spring-security-csrf`在8081端口；
2. 打开浏览器，访问`http://hello:8080`，并完成登录，访问`http://hello:8080/hello`接口，观察项目`springboot-security`后台打印输出；
3. 在同一个浏览器，访问`http://world:8081`，默认进入`index.html`，同时观察项目`springboot-security`后台打印输出；

实验结果：
1. 在同一个浏览器（此处为Firefox，Chrome内核的浏览器未成功）的不同Tab下，在`world`域名下请求`hello`域名下的GET接口/hello：`<img src="http://hello:8080/hello">`，请求成功到达源站后端，实现了CSRF：跨站请求伪造。
2. 验证了`SpringSecurity`虽然默认开启CSRF防护，但是幂等请求诸如"GET", "HEAD", "TRACE", "OPTIONS"被忽略。

### 实验2：CSRF POST攻击

在`spring-security-csrf`项目中模拟一个按钮操作，发起`POST`请求，这里采用原生`JavaScript`发起`Ajax`的`POST`请求：`http://hello:8080/ok`

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
Fishing: <img src="http://hello:8080/hello">

<script language="JavaScript">
    function click() {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "http://hello:8080/ok", true);
        xhr.onload = function (e) {
            console.log("response: ", e.target);
        }
        xhr.onerror = function (e) {
            console.log("error: ", e)
        }
        xhr.send(null);
    }
    click();
</script>
</body>
</html>
```

实验步骤：
1. 启动两个项目：`springboot-security`在8080端口、`spring-security-csrf`在8081端口；
2. 打开浏览器，访问`http://hello:8080`，并完成登录，PostMan访问`http://hello:8080/ok`接口，观察项目`springboot-security`后台打印输出；
3. 在同一个浏览器，访问`http://world:8081`，默认进入`index.html`，同时观察项目`springboot-security`后台打印输出；

![2020-12-08-SpringSecurityCSRF.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-08-SpringSecurityCSRF.png)

实验结果：
1. 在同一个浏览器（此处为Firefox，Chrome内核的浏览器未成功）的不同Tab下，在`world`域名下请求`hello`域名下的POST接口/ok：`http://hello:8080/ok"`，Ajax请求受浏览器同源策略限制，被拦截。CSRF攻击失败。
2. 即使通过`PostMan`访问：`http://hello:8080/ok"`，附带`Cookie`，结果也是被拦截，返回403；
2. 验证了`SpringSecurity`默认开启CSRF防护，对于非幂等请求诸如"POST", "PUT", "DELETE"等请求进行拦截。

因此，相比`GET`请求，`POST`请求相对更安全。

开启了CSRF防护之后，那么问题来了：
1. 我们后端的POST请求都被拦截了，前端难道就没办法发起POST请求了吗？
2. 我们何时需要CSRF防护，何时开启？

这些问题，后续将一一解答。

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***