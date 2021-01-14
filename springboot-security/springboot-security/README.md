### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验`SpringSecurity`的各项Feature。

### 实验0：Hello SpringSecurity
第一步，新建一个`SpringBoot`项目，起名：`springboot-security`，核心依赖为`Web`，此处先不引入`SpringSecurity`依赖。
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
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

建好项目后，创建一个简单的HelloController.java，包含一个`/hello`的`GET`请求:

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "hello springsecurity";
    }
}
```

好了，这时直接启动项目，在浏览器访问：`localhost:8080/hello`返回`hello springsecurity`，表明接口正常。

### 实验1：默认用户名与密码

pom文件中引入`SpringSecurity`依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
然后不做其他任何改动，直接重启项目。

在浏览器访问：`localhost:8080/hello`，这次，接口返回302，跳转到了`http://localhost:8080/login`，要求我们输入用户名与密码完成登录。可问题是，这里的账号密码分别是多少呢？

仔细观察日志，在控制台的启动日志中有如下一行：

    Using generated security password: 26420b20-8ab1-421a-968b-2c537e420527

这表明`SpringSecurity`为我们生成了一个`UUID`形式的密码，默认的用户名为`user`；输入用户名与密码，成功登录后，可以正常访问`/hello`。

因此，仅引入`SpringSecurity`依赖，就实现了对我们后端接口的防护，这便是使用框架的意义：简单、直接、有效。

问题来了，鬼知道它的默认用户名是`user`。。这个可用从官网[https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#servlet-hello-auto-configuration](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#servlet-hello-auto-configuration)或者`SpringBoot`自动配置类的源码查到。

![2020-12-06-SpringSecurityAutoConfig.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-06-SpringSecurityAutoConfig.png)

``` java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.springframework.boot.autoconfigure.security;

import ...

@ConfigurationProperties(
    prefix = "spring.security"
)
public class SecurityProperties {
    public static final int BASIC_AUTH_ORDER = 2147483642;
    public static final int IGNORED_ORDER = -2147483648;
    public static final int DEFAULT_FILTER_ORDER = -100;
    private final SecurityProperties.Filter filter = new SecurityProperties.Filter();
    private final SecurityProperties.User user = new SecurityProperties.User();

    public SecurityProperties() {
    }

    public SecurityProperties.User getUser() {
        return this.user;
    }

    public SecurityProperties.Filter getFilter() {
        return this.filter;
    }

    public static class User {
        private String name = "user";
        private String password = UUID.randomUUID().toString();
        private List<String> roles = new ArrayList();
        private boolean passwordGenerated = true;
    ...
    }    
...
}
```

Note: 最开始引入了`devtools`依赖，目的是能够在配置、代码更新时，能够热启动项目，不用每次都手动停止，再启动，不过在`Idea`中需要进行如下两步，完成配置：

1. `Ctrl+Shift+Alt+/`打开`maintenance`面板，选择第一个`registry`, 勾选`compiler.automake.allow.when.app.running`保存；
2. `Ctrl+Alt+S`, 打开配置面板，在File | Settings | Build, Execution, Deployment | Compiler下勾选`Build project automatically`；

### 实验2：配置文件中覆盖默认的用户名与密码

我们知道，在`application.properties`或者`application.yml`中，可以进行一些自定义配置，对于`SpringSecurity`也不例外。这里以`application.properties`为例，在其中添加如下配置：

```properties
spring.security.user.name=ok
spring.security.user.password=000
```

以上配置用户名为ok，密码为000；为了直接看到效果，我们在浏览器中清除本站的Cookie，或者访问SpringSecurity为我们提供的`/logout`端点，退出登录。

输入实验1中的用户名`user`，密码`26420b20-8ab1-421a-968b-2c537e420527`，发现无法完成登录；
输入`application.properties`中配置的用户名`ok`，密码`000`，登录成功，并实现对`/hello`端点的访问。

Note：其实，还可以通过`UserDetailsManager`或`InMemoryUserDetailsManager`在内存中配置用户名与密码，实现对配置文件中账号信息的覆盖。

### 实验3：内存中覆盖默认的用户名与密码

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     @Bean
     public UserDetailsService userDetailsService(){
         InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
         manager.createUser(User.withUsername("dev").password("123").authorities("p1").build());
         return manager;
     }
}
```

上面这段代码干了这样几件事：
1. 自定义配置类`SecurityConfig`，继承自`WebSecurityConfigurerAdapter`；
2. 定义一个Bean：userDetailsService，其中创建并返回了一个in-memory的用户；
3. `InMemoryUserDetailsManager`创建用户时，指定了用户名、密码、权限（或角色）；

重启项目，浏览器访问：`localhost:8080/hello`，跳转到`http://localhost:8080/login`；键入用户名`dev`，密码`123`，后台报错了：没有密码编码器。。

    java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"

作为一个安全框架，SpringSecurity还是很有原则的，要求使用者必须对密码进行加密，因为[数据泄露](http://www.isccc.gov.cn/xwdt/xwkx/07/903972.shtml)的事件在历史上也是多次发生了。。而且明文存储密码，还可能被偷窥；一旦持久化的数据库暴露，会引发一系列的其他连环事故：比如撞库（讲真，大多数人为了方便记忆，在各网站、平台的密码都是相同的，大家都是肉体凡胎，谁又能记住不同的密码呢），怕了怕了。

解决方法1：我们修改配置，定义一个Bean，返回一个`PasswordEncoder`实例，

```java
@Bean
public PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance(); // Deprecated
}
```

Note：不过上面这个Bean中的`NoOpPasswordEncoder`被标记为`Deprecated`，并且从注释中可以看到，建议这种密码编码器仅作为遗留项目或测试使用，因为作为一个密码编码器，它的`matches`方法其实什么也没干⊙︿⊙

```java
/**
 * This {@link PasswordEncoder} is provided for legacy and testing purposes only and is
 * not considered secure.
 *
 * A password encoder that does nothing. Useful for testing where working with plain text
 * passwords may be preferred.
 *
 * @author Keith Donald
 * @deprecated This PasswordEncoder is not secure. Instead use an adaptive one way
 * function like BCryptPasswordEncoder, Pbkdf2PasswordEncoder, or SCryptPasswordEncoder.
 * Even better use {@link DelegatingPasswordEncoder} which supports password upgrades.
 * There are no plans to remove this support. It is deprecated to indicate that this is a
 * legacy implementation and using it is considered insecure.
 */
@Deprecated
public final class NoOpPasswordEncoder implements PasswordEncoder {

	private static final PasswordEncoder INSTANCE = new NoOpPasswordEncoder();

	private NoOpPasswordEncoder() {
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.toString().equals(encodedPassword);
	}

	/**
	 * Get the singleton {@link NoOpPasswordEncoder}.
	 */
	public static PasswordEncoder getInstance() {
		return INSTANCE;
	}
}
```

解决方法2：这里我们直接在密码处进行编码：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     @Bean
     public UserDetailsService userDetailsService(){
         InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
         PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
         manager.createUser(User.withUsername("dev").password(encoder.encode("123")).authorities("p1").build());
         return manager;
     }
}
```

上述代码增加了编码器，通过`PasswordEncoderFactories`的工厂方法创建一个`PasswordEncoder`实例，实现密码加密；
再次登录，输入用户名`dev`, 密码`123`即可，实现对`/hello`端点的访问。

### 实验4：密码编码器的默认加密方式

```java
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     @Bean
     public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password: {}", encoder.encode("123"));
        manager.createUser(User.withUsername("dev").password(encoder.encode("123")).authorities("p1").build());
        return manager;
     }
}
```

上面代码增加了一行输出，打印加密后的密码：`Password: {bcrypt}$2a$10$4wUnbQvRHsxNuD1MTxdhru7GPANsBh/0Y37fepAduOGQsmtQrMjs.`，可以看到输出的密码前有个特殊说明{bcrypt}，表明密码的加密方式为`bcrypt`，也可以从`PasswordEncoderFactories`类中查看默认的密码加密方式为`bcrypt`。`bcrypt`密码结构见下图：

![2020-12-06-SpringSecurityBcrypt.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-06-SpringSecurityBcrypt.png)

```java
public final class PasswordEncoderFactories {

	private PasswordEncoderFactories() {
	}

	@SuppressWarnings("deprecation")
	public static PasswordEncoder createDelegatingPasswordEncoder() {
		String encodingId = "bcrypt";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(encodingId, new BCryptPasswordEncoder());
		encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
		encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
		encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
		encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder());
		encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
		encoders.put("SHA-256",
				new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
		encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
		encoders.put("argon2", new Argon2PasswordEncoder());
		return new DelegatingPasswordEncoder(encodingId, encoders);
	}
}
```

既然能够作为`SpringSecurity`默认的密码加密方式，`bcrypt`这种加密方法应当是足够安全的，关于`bcrypt`与其他加密算法对比的更多信息可参阅这篇10年前的文章：[https://codahale.com/how-to-safely-store-a-password/](https://codahale.com/how-to-safely-store-a-password/)


### Reference

1. [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)
2. [如何安全地持久化密码？](https://codahale.com/how-to-safely-store-a-password/)
3. [bcrypt密码图解](https://www.jianshu.com/p/2b131bfc2f10)
4. [bcrypt每次生成的hash都不一样，那么它是如何进行校验的？](https://blog.csdn.net/u013810234/article/details/77053011)
5. [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***