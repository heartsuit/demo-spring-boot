### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。

RBAC是什么？Role Based Access Control，关于RBAC的介绍，网上资源很多，这里仅简单描述下。

* 用户-权限：user, user-permission, permission
* 用户-角色-权限：user, user-role, role, role-permission, permission

新建一个 `SpringBoot` 项目，起名 `springboot-security-rbac` ，核心依赖为 `Web` 与 `SpringSecurity` ：

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

### 实验0：基本授权

创建资源接口： `/user/add` ， `/user/query` ，以及默认的home路径 `/` ，用以展示登录用户信息，若未登录，则展示匿名用户信息。

``` java
@RestController
@Slf4j
public class RoleAccessController {
    @GetMapping(value = "/user/add")
    public String accessResource1() {
        return " Access Resource 1: Add User";
    }

    @GetMapping(value = "/user/query")
    public String accessResource2() {
        return " Access Resource 2: Query User";
    }

    @GetMapping(value = "/")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }
}
```

* 在内存中创建两个用户：
  + dev用户具有dev与test角色；
  + test用户仅具有test角色；

* 配置资源授权：
  + /user/add 需要有dev角色才可访问；
  + /user/query 需要有test角色才可访问；

``` java
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = passwordEncoder();

        String yourPassword = "123";
        System.out.println("Encoded password: " + encoder.encode(yourPassword));

        // Config account info and permissions
        auth.inMemoryAuthentication()
                .withUser("dev").password(encoder.encode(yourPassword)).roles("dev", "test")
                .and()
                .withUser("test").password(encoder.encode(yourPassword)).authorities("ROLE_test");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasRole("dev")
                .antMatchers("/user/query").hasAuthority("ROLE_test")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .formLogin();
    }
}
```

#### 实验01：匿名用户

![2020-12-22-Anonymous.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Anonymous.jpg)

此时访问 `/user/add` 或 `/user/query` 都会跳转至登录页面；

#### 实验02：dev用户

![2020-12-22-Dev.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Dev.jpg)

`dev` 用户同时具备 `ROLE_dev` 与 `ROLE_test` 两个角色，此时访问 `/user/add` 或 `/user/query` 都会成功；

#### 实验03：test用户

![2020-12-22-Test1.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Test1.jpg)

![2020-12-22-Test2.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Test2.jpg)

`test` 用户仅具有 `ROLE_test` 角色，此时访问 `/user/add` 失败，访问 `/user/query` 成功；

Note：

* 上述代码中，在分配角色或授权时使用了 `roles()` 与 `authorities()` ，两者区别如下：

1. 使用 `roles()` 时，参数不可加 `ROLE_` 前缀，否则报错。因为 `roles()` 自动会加这个前缀，参考源码 `User类` ：
2. 使用 `authorities()` 时，则没有限制，配合 `hasAuthority` 使用时，权限命名一致即可。

``` java
public UserBuilder roles(String... roles) {
  List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
  for (String role : roles) {
    Assert.isTrue(!role.startsWith("ROLE_"),
        () -> role + " cannot start with ROLE_ (it is automatically added)");
    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
  }
  return authorities(authorities);
}
```

* 另外， `roles()` 与 `authorities()` 不可在一个用户上同时使用，否则会发生覆盖，仅有最后一个会生效，参考源码 `User类` ：

![2020-12-22-Roles.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Roles.jpg)

![2020-12-22-Authorities.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Authorities.jpg)

### 实验1：方法授权-securedEnabled

在启动类或配置类上添加注解： `@EnableWebSecurity` 与 `@EnableGlobalMethodSecurity(securedEnabled=true)` ，表示启用 `SpringSecurity` 默认的方法授权。

``` java
@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SpringbootSecurityRbacApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootSecurityRbacApplication.class, args);
    }
}
```

为了方便演示，这里新建一个Controller： `SecuredAccessController` ，资源请求以及用户授权都与上一个实验相同，这里可以删除 `SecurityConfig` 类中关于 `http` 请求权限的配置。

``` java
@RestController
@RequestMapping("/user0")
@Slf4j
public class SecuredAccessController {
    @GetMapping(value = "/add")
    @Secured({"ROLE_dev", "ROLE_test"}) // only support OR relation
    public String accessResource1() {
        return " Access Resource 1: Add User";
    }

    @GetMapping(value = "/query")
    @Secured({"ROLE_test"})
    public String accessResource2() {
        return " Access Resource 2: Query User";
    }

    @GetMapping(value = "/")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }
}
```

结果如下：

* dev用户可以访问 `/user0/add` 与 `/user0/query` ；
* test用户也可以访问 `/user0/add` 与 `/user0/query` ；

Note: 

1. `@Secured({"ROLE_dev", "ROLE_test"})` 这里采用了角色组合，是“或”的关系。
2. 鉴于 `SpringSecurity` 默认的方法授权的局限性，实际中更多地会使用 `PreAuthorize` ，可以实现“或”、“与”的关系，支持 `Spring EL表达式` ，看下个实验。

### 实验2：方法授权-prePostEnabled

配置类的注解增加属性配置 `prePostEnabled=true` ，变成： `@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)` 。

为了方便演示，这里新建一个Controller： `PrePostAccessController` ，资源请求以及用户授权都与上一个实验相同，这里可以删除 `SecurityConfig` 类中关于 `http` 请求权限的配置。

``` java
@RestController
@RequestMapping("/user1")
@Slf4j
public class PrePostAccessController {
    @GetMapping(value = "/add")
//    @PreAuthorize("hasRole('ROLE_dev')")
    @PreAuthorize("hasRole('dev')")
//    @PreAuthorize("hasAnyRole('ROLE_dev', 'ROLE_test')")
//    @PreAuthorize("hasRole('ROLE_dev') and hasRole('ROLE_test')")
    public String accessResource1() {
        return " Access Resource 1: Add User";
    }

    @GetMapping(value = "/query")
    @PreAuthorize("hasAuthority('ROLE_test')")
    public String accessResource2() {
        return " Access Resource 2: Query User";
    }

    @GetMapping(value = "/")
//    @PreAuthorize("authenticated")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping(value = "/res")
    @PostAuthorize("returnObject==true")
    public boolean response() {
        int i = new Random().nextInt();
        log.info("Response, {}", i);
        return i > 0;
    }
}
```

分别演示 `PreAuthorize` 本身及多个权限组合与 `PostAuthorize` ，可打开注释进行测试，具体结果：略。

### 实验3：方法授权-jsr250Enabled 

其实，除了 `securedEnabled` 与 `prePostEnabled` ， `@EnableGlobalMethodSecurity` 还有第三个选项： `jsr250Enabled` 

![2020-12-22-Global.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Global.jpg)

具体使用方法，此处不作演示了。

Note：虽然 `SpringSecurity` 的 `@EnableGlobalMethodSecurity` 注解提供了三种选项来使用方法级别的授权，但是实际使用时不建议混合使用，参考官方文档关于这一点的说明：

![2020-12-22-Suggestion.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-22-Suggestion.jpg)

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***
