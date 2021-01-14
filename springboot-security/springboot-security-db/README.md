### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。

之前涉及到的用户信息都是存在内存中的，显然，这种方法用于测试或演示还可以，实际中的应用场景肯定要求从数据库中读取的。

新建一个 `SpringBoot` 项目，起名 `springboot-security-db` ，核心依赖为 `Web` , `SpringSecurity` , `Thymeleaf` 及 `MyBatis` ：

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
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
            <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.21</version>
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

### 实验0：官方默认的用户权限表

`SpringSecurity` 官方文档提供了默认的 `User Schema` ，比较简单，直接是用户及其权限关系，这里不作演示；

![2020-12-24-ddl.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-12-24-ddl.png)

### 实验1：自定义用户权限表

* 首先看下表结构，是实际应用中相对比较通用的，共5张表，遵循了RBAC的模式：三个抽象实体（用户、角色、权限）表，两张关系表（用户-角色，角色-权限），即:

> user, user-role, role, role-permission, permission

``` sql
CREATE DATABASE IF NOT EXISTS `spring_security` ;
USE `spring_security` ;

CREATE TABLE IF NOT EXISTS `t_user` (
`id` bigint(20) NOT NULL COMMENT '用户id',
`username` varchar(64) NOT NULL,
`password` varchar(64) NOT NULL,
`realname` varchar(255) NOT NULL COMMENT '真实姓名',
`mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
`enabled` tinyint(1) DEFAULT NULL COMMENT '是否启用',
`accountNonExpired` tinyint(1) NOT NULL DEFAULT '1',
`accountNonLocked` tinyint(1) NOT NULL DEFAULT '1',
`credentialsNonExpired` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY ( `id` ) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE IF NOT EXISTS `t_user_role` (
`user_id` varchar(32) NOT NULL,
`role_id` varchar(32) NOT NULL,
`create_time` datetime DEFAULT NULL,
`creator` varchar(255) DEFAULT NULL,
  PRIMARY KEY ( `user_id` , `role_id` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `t_role` (
`id` varchar(32) NOT NULL,
`role_name` varchar(255) DEFAULT NULL,
`description` varchar(255) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`update_time` datetime DEFAULT NULL,
`status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY ( `id` ),
  UNIQUE KEY `unique_role_name` ( `role_name` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `t_role_permission` (
`role_id` varchar(32) NOT NULL,
`permission_id` varchar(32) NOT NULL,
  PRIMARY KEY ( `role_id` , `permission_id` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `t_permission` (
`id` varchar(32) NOT NULL,
`code` varchar(32) NOT NULL COMMENT '权限标识',
`description` varchar(64) DEFAULT NULL COMMENT '描述',
`url` varchar(128) DEFAULT NULL COMMENT '请求地址',
  PRIMARY KEY ( `id` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

* 基本配置

``` yml
server:
  port: 8080
spring:
  thymeleaf:
    cache: false
  datasource:
    url: jdbc:mysql://localhost:3306/spring_security?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```

* 从以前的内存用户换为从数据库中读取用户

这里我们实现 `UserDetailsService` 接口，重写 `loadUserByUsername(String username)` 方法，基本逻辑：查询用户，及联表查询对应的权限。

``` java
@Component
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired
  private UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDto user = userMapper.getUserByUsername(username);
    if (user != null) {
      List<PermissionDto> permissions = userMapper.getPermissionsByUsername(username);
      if (permissions != null) {
        System.out.println(user.getUsername() + " has these permissions: " + permissions);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        permissions.stream().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode())));
        // user.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("p1"))); // hard-coded permission
        user.setAuthorities(authorities);
      }
    }
    return user;
  }
}
```

相关查询接口：

``` java
public interface PermissionMapper {
  @Select("SELECT * FROM t_permission")
  List<PermissionDto> getAllPermissions();
}

public interface UserMapper {
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    UserDto getUserByUsername(@Param("username") String username);

    /*
    - SELECT p.* FROM t_permission p LEFT JOIN t_role_permission rp ON p.id = rp.permission_id
    LEFT JOIN t_user_role ur ON rp.role_id = ur.role_id
    LEFT JOIN t_user u ON ur.user_id = u.id
    WHERE u.username = "test";
    - */

    @Select("SELECT p.* FROM t_permission p LEFT JOIN t_role_permission rp ON p.id = rp.permission_id LEFT JOIN t_user_role ur ON rp.role_id = ur.role_id LEFT JOIN t_user u ON ur.user_id = u.id WHERE u.username = #{username};")
    List<PermissionDto> getPermissionsByUsername(@Param("username") String username);
}
```

将用户数据源配置为从数据库中查询：

``` java
@Autowired
CustomUserDetailsService customUserDetailsService;

@Bean
public PasswordEncoder passwordEncoder () {
    return new BCryptPasswordEncoder();
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Method1:
    // There is no PasswordEncoder mapped for the id "null"
    // PasswordEncoder encoder = new BCryptPasswordEncoder();        
    // String yourPassword = "123";
    // System.out.println("Encoded password: " + encoder.encode(yourPassword));
    // auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder);

    auth.userDetailsService(customUserDetailsService);
}
```

* 从以前的硬编码的权限控制换为动态配置每个资源的权限

``` java
@Override
protected void configure(HttpSecurity http) throws Exception {
    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http
    .authorizeRequests();
    
    List<PermissionDto> permissions = permissionMapper.getAllPermissions();

    for (PermissionDto permission : permissions) {
        authorizeRequests.antMatchers(permission.getUrl()).hasAuthority(permission.getCode());
    }
    authorizeRequests
            .antMatchers("/user/**").authenticated()
            .anyRequest().permitAll() // Let other request pass
            .and()
            .csrf().disable() // turn off csrf, or will be 403 forbidden
            .formLogin() // Support form and HTTPBasic
            .loginPage("/login")
            .successForwardUrl("/greeting")// custom login success page, a POST request
            .failureHandler(failureHandler)
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout");
}
```

关键的改动：

``` java
List<PermissionDto> permissions = permissionMapper.getAllPermissions();

for (PermissionDto permission : permissions) {
    authorizeRequests.antMatchers(permission.getUrl()).hasAuthority(permission.getCode());
}
```

至此，便实现了从数据库中读取用户权限信息，符合实际的应用场景，并借助SpringSecurity实现动态的权限拦截配置，之后可进行以前的实验：

* 在数据库中添加两个用户：
  + dev用户具有dev与test角色；
  + test用户仅具有test角色；

* 配置资源授权：
  + /user/add 需要有dev角色才可访问；
  + /user/query 需要有test角色才可访问；

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***