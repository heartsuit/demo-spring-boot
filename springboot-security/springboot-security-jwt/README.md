### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。

会话管理，是一个比较大的话题，大家熟知的`Cookie-Session`模式就忽略掉，今天重点介绍无状态会话：基于令牌的`JWT`（JSON Web Token），适用于微服务架构的会话管理方式；后续会涉及到`Session`共享、`OAuth2.0`等关于分布式集群的会话管理。

### JWT简介

关于JWT的介绍，网上资源有很多，可参考：[https://jwt.io/introduction](https://jwt.io/introduction)，简单来说，JWT由三部分构成：Header、Payload、Signature，三者之间以点号. 分隔，前面两部分使用Base64编码（[关于Base64编码的更多信息](https://blog.csdn.net/u013810234/article/details/111825761)），具体构成如下：

![2021-01-13-JWTParsed.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWTParsed.png)

``` java
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

### 无状态与有状态

* 有状态服务

有状态服务，即服务端记录每次会话的客户端信息，从而识别客户端身份，根据用户身份进行相应的处理，`HTTP`本身是无状态的，短连接，因此便有了我们传统的`Cookie-Session`模式，这在单体架构中广泛使用。用户完成登录后，与用户的会话信息被保存在服务端的`Session`中，然后服务端响应一个`SessionID`给前端，前端将这个`SessionID`存储在`Cookie`中，后续请求携带`Cookie`信息继续发起请求，后端再查询其对应的会话信息，完成请求响应。

这种方式在微服务架构下会带来一些问题：

1. 每建立一个会话，服务端就需要存储这个会话信息，增加了服务端存储、查询的压力，占用了宝贵的存储、计算资源；
2. 服务端保存用户状态，难以进行水平扩展，需要在各服务器上进行状态的复制、同步（Session同步、Session共享）等处理后才能进行扩展；

* 无状态服务

明白了有状态服务，无状态服务就好理解了，无状态服务在实际比较常见的实现是采用基于令牌的方式，即：

1. 服务端不保存任何客户端会话信息；
2. 客户端的每次请求必须携带令牌，其中包含了认证者、签名相关信息（用户名、角色、权限等）；

### JWT优点、缺点

* 使用JWT进行认证处理具有以下优点：

    - JWT是基于令牌的，将用户状态分散到了客户端中，服务器端无状态，减轻了服务器的压力，提升了性能；
    - JWT具有严格的结构化，其自身就包含了关于认证用户的相关消息， 一旦校验成功，那么资源服务器就无须再去认证服务器验证信息的有效性；
    - JWT中的载荷可以支持定制化，因此开发者可以根据业务需要进行扩展定义，如添加用户是否是管理员、用户所在分桶等信息，从而满足业务需要；
    - JWT体积小，便于传输，并且在传输方式上支持URL/POST参数或者HTTP头部等方式传输，因而可以支持多种客户端，不仅仅是Web；
    - JWT使用JSON格式，对跨语言的支持非常好；
    - JWT支持跨域，使单点登录的开发更容易。

* 以下几点是在实施JWT安全方案时需要仔细考虑的问题：

    - JWT令牌注销：由于JWT令牌存储在客户端，当用户注销时可能由于有效时间还没有到，造成客户端汪会存储，这时候需要开发者能够有效防止注销后令牌的访问，开发者可以借助API 网关来实现。另外，采用短期令牌也是一个不错的解决方案。
    - JWT令牌超长： 由于JWT允许开发者对令牌进行自定义扩展，如果在JWT的载荷中包含的信息过多，就会导致客户端每次的请求头部信息变长，从而影响请求速度。
    - 避免成为系统新瓶颈：由于API网关服务会对认证服务器进行访问及鉴权处理，有可能会形成系统的新瓶颈。
    - 需有效防范XSS攻击：由于JWT存储在客户端，最有可能引发XSS攻击，因此当使用JWT时必须做出有效的防范。

老规矩，依然采用实验的方式来进行测试，不过这次先看效果：

### 实验0：登录获取JWT

![2021-01-13-JWTLogin.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWTLogin.png)

### 实验1：携带JWT：正常响应

![2021-01-13-JWT200.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWT200.png)

### 实验2：未携带JWT：未认证响应

![2021-01-13-JWT401.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWT401.png)

### 实验3：携带JWT：无权限响应

![2021-01-13-JWT403.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWT403.png)

### 实验4：携带JWT：过期响应

![2021-01-13-JWTExpired.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWTExpired.png)

### 实验5：携带JWT：非法格式响应

![2021-01-13-JWTWrong.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-JWTWrong.png)

### 编码实现

新建一个 `SpringBoot` 项目，起名 `springboot-security-jwt` ，核心依赖为 `Web` , `SpringSecurity` 及 `jjwt` ：

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
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.7.0</version>
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

![2021-01-13-Project.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-13-Project.png)

创建资源接口： `/user/add` ， `/user/query` ，以及默认的home路径 `/` ，用以展示登录用户信息，若未登录，则展示匿名用户信息。

``` java
@RestController
@Slf4j
public class HelloController {
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

安全配置类：

``` java
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;

    public SecurityConfig(JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint authenticationErrorHandler) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.authenticationErrorHandler = authenticationErrorHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = passwordEncoder();

        String yourPassword = "123";
        log.info("Encoded password: " + encoder.encode(yourPassword));

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
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless
                .and()
                .formLogin()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
                        log.info("Login Successfully");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String token = JwtUtil.createToken(authentication);
                        httpServletResponse.getWriter().write(token);
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
                        log.info("Login Error");
                        httpServletResponse.getWriter().write(e.getLocalizedMessage());
                    }
                })
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler);
    }
}
```

JWT过滤器类： `JwtAuthenticationFilter` 

``` java
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 从头信息中提取token
        String token = JwtUtil.resolveToken(request);
        if (token != null) {
            // 通过JWT工具方法解析token
            Authentication authentication = JwtUtil.getAuthentication(token);
            // 将认证信息设置到上下文中，注意无状态(stateless)的设置！
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
```

未认证、未授权拦截类： `JwtAuthenticationEntryPoint` ， `JwtAccessDeniedHandler` 

``` java
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        // 当用户尝试访问安全的REST资源而不提供任何凭据时，将调用此方法发送401 响应
        log.info("UNAUTHORIZED");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
}

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        // 当用户在没有授权的情况下访问受保护的REST资源时，将调用此方法发送403 Forbidden响应
        log.info("FORBIDDEN");
        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }
}
```

JWT工具类与常量类： `JwtUtil` ， `JwtConstant` 

``` java
public class JwtUtil {
    /**
     * 生成 jwt token
     *
     * @param authentication
     * @return
     */
    public static String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + JwtConstant.VALIDITY_SECONDS * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(JwtConstant.AUTH_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, JwtConstant.SECRET)
                .setExpiration(validity)
                .compact();
    }

    /**
     * 解密 jwt token
     *
     * @param token
     * @return
     */
    public static Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JwtConstant.SECRET)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(JwtConstant.AUTH_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 从请求头信息中解析出token
     *
     * @param request
     * @return
     */
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

public final class JwtConstant {
    /**
     * 对称加密密钥
     * 仅服务端存储，生产中建议使用复杂度高的密钥或采用非对称加密eg:RSA
     */
    public static final String SECRET = "heartsuit";

    /**
     * Token有效期
     */
    public static final long VALIDITY_SECONDS = 60 * 60 * 12; // default 12 hours

    /**
     * 权限
     */
    public static final String AUTH_KEY = "auth";

    /**
     * 头信息中Token的Key
     */
    public static final String HEADER = "authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    private JwtConstant() {
    }
}
```

### 距离生产有多远

1. formLogin，为了方便演示，这里有点取巧，其实无法通过浏览器表单登录了，而且当今的实际项目一般都是前后端分离的。这里在登录成功后生成JWT直接放到了 `formLogin` 的 `successHandler` 中，仅供演示。

2. 内存方式，用户信息（用户名、密码、权限）使用简单的内存型存储；实际生产应使用数据库，方便扩展；

3. 权限硬编码，在安全配置中，权限拦截通过手动编写；实际生产应从数据库中查询出来后动态配置；

4. 配置常量，关于JWT的配置信息，使用了一个常量类；实际中可写到配置文件（或配置中心），通过 `SpringBoot` 的配置属性进行读取；

5. 对称加密，这里采用的是对称加密方式对JWT进行签名与验签；实际生产中建议使用非对称加密算法eg: `RSA` 等实现；

6. 异常拦截，关于JWT过期、格式错误的异常都是JWT依赖包自动抛出的异常，实际生产中应进行拦截并进一步封装，优化接口调用体验；

如果上述每一点占10%，那么距离上生产还有一多半的工作量要完成，因此，前面的实验或代码权当是纸上谈兵，仅用于演示，意思到了就足够了。。

### 注意事项

1. 务必关闭csrf防护

`.csrf().disable()` 
   

2. 务必采用stateless方式

`.sessionCreationPolicy(SessionCreationPolicy.STATELESS)` 

### Source Code

[Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-jwt)

### Reference

* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)
* [JWT简介](https://jwt.io/introduction)
* [JWT调试](https://jwt.io/#debugger-io)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***