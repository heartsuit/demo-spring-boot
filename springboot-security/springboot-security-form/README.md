### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验`SpringSecurity`的各项Feature。

新建一个`SpringBoot`项目，起名`springboot-security-form`，核心依赖为`Web`，`SpringSecurity`与`Thymeleaf`。

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

### 实验0：HttpBasic

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // There is no PasswordEncoder mapped for the id "null"
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    
    String yourPassword = "123";
    System.out.println("Encoded password: " + encoder.encode(yourPassword));

    // Config account info and permissions
    auth.inMemoryAuthentication()
    .withUser("dev").password(encoder.encode(yourPassword)).authorities("p1")
    .and()
    .withUser("test").password(encoder.encode(yourPassword)).authorities("p2");
}

@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .httpBasic();
}
```

![2020-12-11-HttpBasic.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-11-HttpBasic.jpg)


### 实验1：自定义登录页面

- 登录页面配置

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login");
}
```

- 后端登录页面接口

```java
@Controller
public class LoginController {
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping(value = "/user/add")
  @ResponseBody
  public String accessResource1() {
    return " Access Resource 1: Add User";
  }

  @GetMapping(value = "/user/query")
  @ResponseBody
  public String accessResource2() {
    return " Access Resource 2: Query User";
  }
}
```

- 前端模板

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<form action="login" method="post">
    <span>用户名</span><input type="text" name="username" /> <br>
    <span>密码</span><input type="password" name="password" /> <br>
    <input type="submit" value="登录">
</form>
</body>
</html>
```

Note：此时，需要先关闭CSRF，`.csrf().disable()`，否则报403；

### 实验2：自定义登录接口

默认登录页面接口与登录数据提交接口是同一个：`/login`，顺着`.loginPage`，进入`FormLoginConfigurer`，源码如下：

```java
@Override
public FormLoginConfigurer<H> loginPage(String loginPage) {
    return super.loginPage(loginPage);
}
```

继续进入父类的`loginPage`方法，

```java
protected T loginPage(String loginPage) {
    setLoginPage(loginPage);
    updateAuthenticationDefaults();
    this.customLoginPage = true;
    return getSelf();
}
```

继续跟踪进入方法`updateAuthenticationDefaults();`，可以看到，如果没有配置`loginProcessingUrl`，那么`loginProcessingUrl`与`loginPage`便相同。

```java
protected final void updateAuthenticationDefaults() {
    if (loginProcessingUrl == null) {
        loginProcessingUrl(loginPage);
    }
    if (failureHandler == null) {
        failureUrl(loginPage + "?error");
    }

    final LogoutConfigurer<B> logoutConfigurer = getBuilder().getConfigurer(
            LogoutConfigurer.class);
    if (logoutConfigurer != null && !logoutConfigurer.isCustomLogoutSuccess()) {
        logoutConfigurer.logoutSuccessUrl(loginPage + "?logout");
    }
}
```

下面我们自定义登录数据提交接口为`/formLogin`，此时相应的前端action也要修改。

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .loginProcessingUrl("/formLogin");
}
```

```html
<form action="formLogin" method="post">
    <span>用户名</span><input type="text" name="username" /> <br>
    <span>密码</span><input type="password" name="password" /> <br>
    <input type="submit" value="登录">
</form>
```

### 实验3：自定义登录数据参数

- 前面我们自定义了登录页面，在`form`表单中设置用户名、密码分别为`username`, `password`，那为什么这样写呢，可以改成别的嘛？可以倒是可以，但是不能随便改；
- 如果这里我们把`username`改为`name`，再次尝试登录，后端接口将报错：`org.springframework.security.authentication.BadCredentialsException: Bad credentials`。。可是实际项目中我们的用户名密码就是不叫这个名字呢？我们可以进行配置`.usernameParameter("name")`：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .loginProcessingUrl("/formLogin")
            .usernameParameter("name");
}
```

```html
<form action="formLogin" method="post">
    <span>用户名</span><input type="text" name="name" /> <br>
    <span>密码</span><input type="password" name="password" /> <br>
    <input type="submit" value="登录">
</form>
```

默认的用户名、密码分别为`username`, `password`，我们看下SpringSecurity的源码：

```java
public final class FormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
		AbstractAuthenticationFilterConfigurer<H, FormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter> {
	/**
	 * Creates a new instance
	 * @see HttpSecurity#formLogin()
	 */
	public FormLoginConfigurer() {
		super(new UsernamePasswordAuthenticationFilter(), null);
		usernameParameter("username");
		passwordParameter("password");
	}
}
```

### 实验4：自定义登录失败、成功处理器

问题：就以上个实验3中的报错信息为例，或当用户名、密码输错后，如何在后台看到错误信息？

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .loginProcessingUrl("/formLogin")
            .usernameParameter("name")
            .failureHandler(new AuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                    exception.printStackTrace();
                    request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
                }
            });
}
```

常见的认证异常，这里可以看到`AuthenticationException`共有18个子类：

![2020-12-11-AuthenticationFailure.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-11-AuthenticationFailure.jpg)

上述增加了在认证失败时的处理：输出错误信息。同理，如果想在登录成功时直接进行一些处理（eg: 数据初始化等），可以使用以下配置：

```java
.successHandler(new AuthenticationSuccessHandler() {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        System.out.println("Login Successfully~");
        // do something here: initial work or forward to different url regarding different roles
        ...
        request.getRequestDispatcher("").forward(request, response);
    }
})
```

### 实验5：自定义登录成功跳转页面

经历千难万险，终于要登录成功了。进来之后要跳转到哪里呢？看你喽~想跳哪里跳哪里。。

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .loginProcessingUrl("/formLogin")
            .usernameParameter("name")
            .failureHandler(new AuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                    exception.printStackTrace();
                    request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
                }
            })
            .successForwardUrl("/ok"); // custom login success page, a POST request
}
```

```java
@Controller
public class LoginController {
  ...

  @PostMapping(value = "/ok")
  @ResponseBody
  public String ok() {
    return "ok";
  }
}
```

通过`.successForwardUrl("/ok")`配置了登录成功之后要跳转的页面路径或接口，同时需要在后端新增`/ok`接口。

Note: 
- 注意这里`successForwardUrl`的接口必须为`POST`接口；
- 除了`.successForwardUrl("/ok");`，还可以使用`.defaultSuccessUrl("/ok");`或者`.defaultSuccessUrl("/ok", true);` 第二个参数`true`表示不管是从哪个地址进来，登录后全部跳转到指定的地址，此时与`successForwardUrl`效果相同，默认为`false`，
- 当然，除了登录成功后的跳转，还有登录失败后的跳转：`failureForwardUrl`。

### 实验6：自定义退出接口

默认的退出接口是`/logout`，可进行配置：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/user/add").hasAuthority("p1")
            .antMatchers("/user/query").hasAuthority("p2")
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .loginProcessingUrl("/formLogin")
            .usernameParameter("name")
            .failureHandler(new AuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                    exception.printStackTrace();
                    request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
                }
            })
            .successForwardUrl("/ok") // custom login success page, a POST request
            .and()
            .logout()
            .logoutUrl("/leave");            
}
```

上述配置将退出接口改为`/leave`。在默认的退出过程中，还做了诸如清除认证信息和使Session失效等工作：

```java
public class SecurityContextLogoutHandler implements LogoutHandler {
	protected final Log logger = LogFactory.getLog(this.getClass());

	private boolean invalidateHttpSession = true;
	private boolean clearAuthentication = true;

	// ~ Methods
	// ====================================================
    
	/**
	 * Requires the request to be passed in.
	 *
	 * @param request from which to obtain a HTTP session (cannot be null)
	 * @param response not used (can be <code>null</code>)
	 * @param authentication not used (can be <code>null</code>)
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Assert.notNull(request, "HttpServletRequest required");
		if (invalidateHttpSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				logger.debug("Invalidating session: " + session.getId());
				session.invalidate();
			}
		}
		if (clearAuthentication) {
			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(null);
		}
		SecurityContextHolder.clearContext();
	}
}
```

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***