## CSRF 攻击

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


## CSRF 防护

### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验`SpringSecurity`的各项Feature。

接着上一篇文章[3-SpringSecurity：自定义Form表单](https://blog.csdn.net/u013810234/article/details/111054094)中的项目：`spring-security-form`，继续演示开启`CSRF`防护的场景（当时关闭了CSRF：.csrf().disable()）。

依赖不变，核心依赖为`Web`，`SpringSecurity`与`Thymeleaf`：

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
        <artifactId>spring-boot-starter-security</artifactId>
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

从官网中可以知道，CSRF防护的关键在于我们发请求时附带一个随机数（CSRF token），而这个随机数不会被浏览器自动携带（eg: Cookie就会被浏览器自动带上）。

![2020-12-13-AntiCSRF.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-AntiCSRF.png)

### 实验0：登录时的CSRF防护

显然，我们这里的登录请求是个`POST`方法（`SpringSecurity`默认忽略"GET", "HEAD", "TRACE", "OPTIONS"等幂等请求的`CSRF`拦截）。登录时必须携带`_csrf`参数，与认证信息一并提交，否则报403。

- 后端安全配置（默认开启`CSRF`）

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            // .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .failureHandler(new AuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                    exception.printStackTrace();
                    request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
                }
            });            
}
```

- 前端模板（新增了`_csrf`参数）：

```html
<form action="login" method="post">
    <span>用户名</span><input type="text" name="username" /> <br>
    <span>密码</span><input type="password" name="password" /> <br>
    <span>csrf token</span><input type="text" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/> <br>
    <input type="submit" value="登录">
</form>
```
Note: 
1. 当然，实际中可以将新增的`_csrf`参数作为一个隐藏域进行提交：`<input type="text" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" hidden/>`
2. 其实，如果我们使用默认的登录页面，可以在页面元素中看到同样有个隐藏域：

![2020-12-13-CSRFHidden.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-CSRFHidden.png)

### 实验1：POST接口CSRF防护

通过form表单是一种发送POST请求的方式，但我们其他的请求不可能都通过form表单来提交。下面通过原生的JavaScript发起Ajax的POST请求。

- 后端接口

```java
@Controller
public class HelloController {
    @RequestMapping("/")
    public String hello(){
        return "index";
    }
    
    @PostMapping(value = "/ok")
    @ResponseBody
    public String ok() {
        return "ok post";
    }    
}
```

- 前端模板（新增index.html）

```html
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <meta name="csrf" th:content="${_csrf.token}">
  <meta name="_csrf_header" th:content="${_csrf.headerName}" />
  <title>SpringSecurity</title>
</head>

<body>
  <a href="/user/add">添加用户</a>
  <a href="/user/query">查询用户</a>
  <a href="/logout">退出</a>

  <script language="JavaScript">
    // let token = document.getElementsByTagName('meta')['csrf'].content;
    let token = document.querySelector('meta[name="csrf"]').getAttribute('content');
    let header = document.getElementsByTagName('meta')['_csrf_header'].content;
    console.log("token: ", token);
    console.log("header: ", header);

    function click() {
      let xhr = new XMLHttpRequest();
      xhr.open("POST", "http://localhost:8080/ok", true);
      xhr.setRequestHeader(header, token);
      xhr.onload = function (e) {
        console.log("response: ", e.target.responseText);
      }
      xhr.onerror = function (e) {
        console.log("error: ", e)
      }
      xhr.send(null);
    }
    click();
  </script>
</body>
```

![2020-12-13-RequestHeader.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-RequestHeader.png)

![2020-12-13-CSRFPrint.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-CSRFPrint.png)

Note: 前面这两个实验中用到了一些参数：`_csrf.parameterName`，`_csrf.token`，`_csrf_header`等，这些可以从源码中获悉：

```java
public final class HttpSessionCsrfTokenRepository implements CsrfTokenRepository {
	private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

	private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class
			.getName().concat(".CSRF_TOKEN");

	private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

	private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;
}    
```

### 实验2：退出时的CSRF防护

退出url在开启CSRF之后，直接以a标签形式请求/logout（即GET方式）会报404；此时logout必须以POST方式才可以正常退出。

```java
public final class LogoutConfigurer<H extends HttpSecurityBuilder<H>> extends
		AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
	private List<LogoutHandler> logoutHandlers = new ArrayList<>();
	private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
	private String logoutSuccessUrl = "/login?logout";
	private LogoutSuccessHandler logoutSuccessHandler;
	private String logoutUrl = "/logout";
	private RequestMatcher logoutRequestMatcher;
	private boolean permitAll;
    private boolean customLogoutSuccess;
    ...

	/**
	 * The URL that triggers log out to occur (default is "/logout"). If CSRF protection
	 * is enabled (default), then the request must also be a POST. This means that by
	 * default POST "/logout" is required to trigger a log out. If CSRF protection is
	 * disabled, then any HTTP method is allowed.
	 *
	 * <p>
	 * It is considered best practice to use an HTTP POST on any action that changes state
	 * (i.e. log out) to protect against <a
	 * href="https://en.wikipedia.org/wiki/Cross-site_request_forgery">CSRF attacks</a>. If
	 * you really want to use an HTTP GET, you can use
	 * <code>logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl, "GET"));</code>
	 * </p>
	 *
	 * @see #logoutRequestMatcher(RequestMatcher)
	 * @see HttpSecurity#csrf()
	 *
	 * @param logoutUrl the URL that will invoke logout.
	 * @return the {@link LogoutConfigurer} for further customization
	 */
	public LogoutConfigurer<H> logoutUrl(String logoutUrl) {
		this.logoutRequestMatcher = null;
		this.logoutUrl = logoutUrl;
		return this;
	}    
}
```

可采用form表单或者Ajax的形式发送POST请求，携带`_csrf`参数，这里以form表单为例，点击`POST logout`按钮，可成功退出：

```html
<form action="logout" method="post">
    <input type="text" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" hidden/> <br>
    <input type="submit" value="POST logout">
</form>
```

### 实验3：前后端分离时的CSRF防护

前面是通过在模板引擎中接收后端传回的`_csrf`，这里演示下前后端分离项目如何实现CSRF防护下的安全请求。

> A CsrfTokenRepository that persists the CSRF token in a cookie named "XSRF-TOKEN" and reads from the header "X-XSRF-TOKEN" following the conventions of AngularJS. When using with AngularJS be sure to use withHttpOnlyFalse().

- 后端安全配置（修改`CSRF`存储类型：CookieCsrfTokenRepository）

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            // .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .failureHandler(new AuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                    exception.printStackTrace();
                    request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
                }
            });            
}
```

- 前端脚本

```html
</body>
  <script>
    function getCookie(name) {
      let arr = document.cookie.split("; ");
      for (let i = 0; i < arr.length; i++) {
        let arr2 = arr[i].split("=");
        if (arr2[0] == name) {
          return arr2[1];
        }
      }
      return "";
    }
    console.log("XSRF-TOKEN: ", getCookie("XSRF-TOKEN"));
    // 之后就可以拿着前面获取到的"XSRF-TOKEN"去请求后端POST等接口了
  </script>
</body>    
```

![2020-12-13-Cookie.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-Cookie.png)

Note: 这里大部分同学有个问题：Cookie都被自动带到请求中了，那攻击者不就又可以拿到了吗？

> 由于`Cookie`中的信息对于攻击者来说是不可见的，无法伪造的，虽然Cookie被浏览器**自动**携带了，但攻击者能做的仅仅是用一下`Cookie`，而`Cookie`里面到底放了什么内容，攻击者是不知道的，所以将`CSRF-TOKEN`写在`Cookie`中是可以防御`CSRF`的，相比默认的存放在`Session`中，`CSRF-TOKEN`写在`Cookie`中仅仅是换了一个存储位置。

### 什么时候需要开启CSRF？

![2020-12-13-WhenCSRF.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-13-WhenCSRF.png)

官方文档建议，但凡涉及到浏览器用户操作，均应启用`CSRF`防护。

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
- [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)
- [SpringSecurity官方API](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/csrf/CookieCsrfTokenRepository.html)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***