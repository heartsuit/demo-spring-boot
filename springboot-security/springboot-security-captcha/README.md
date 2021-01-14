### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。

实际项目中，为防止一般的恶意攻击，在认证时除了用户名、密码之外，我们还会要求用户输入验证码，今天我们就在 `SpringSecurity` 用户名-密码认证前，强行进行图形验证码的核验。

Note：当前数字、文本、图片验证码均已不安全，人机交互、短信验证码相对安全。

新建一个 `SpringBoot` 项目，起名 `springboot-security-captcha` ，核心依赖为 `Web` , `SpringSecurity` , `Thymeleaf` 及 `easy-captcha` ：

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
        <groupId>com.github.whvcse</groupId>
        <artifactId>easy-captcha</artifactId>
        <version>1.6.2</version>
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

### 效果演示

![2021-01-02-Login.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-02-Login.png)

![2021-01-02-UserInfo.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-02-UserInfo.png)

### 登录接口及验证码核验

除了基本的资源资源接口： `/user/add` ， `/user/query` ，以及默认的home路径 `/` ，用以展示登录用户信息；
增加 `/login` 接口返回带验证码的登录页面，另外，登录接口改为 `/signin` ，完成验证码、用户信息的核验。

这里采用[EasyCaptcha](https://gitee.com/whvse/EasyCaptcha)，支持gif、中文、算术等类型。

![2021-01-02-EasyCaptcha.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-02-EasyCaptcha.png)

``` java
@Controller
@Slf4j
public class LoginController {
    // 模拟数据库或缓存，存储验证码
    private Map<String, String> captchaStore = new ConcurrentHashMap<>();

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        // gif类型
//        GifCaptcha captcha = new GifCaptcha(130, 48);

        // 中文类型
//        ChineseCaptcha captcha = new ChineseCaptcha(130, 48);

        // 中文gif类型
//        ChineseGifCaptcha captcha = new ChineseGifCaptcha(130, 48);

        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);

        // 几位数运算(默认是两位)
        captcha.setLen(2);

        // 获取生成的文本
        String code = captcha.text().equals("0.0") ? "0" : captcha.text();
        // 验证码对应的UUID
        String uuid = UUID.randomUUID().toString();

        log.info("Captcha generated: {}, UUID generated:{}", code, uuid);

        // 保存生成的验证码信息
        captchaStore.put(uuid, code);

        // 返回验证码信息
        model.addAttribute("img", captcha.toBase64());
        model.addAttribute("uuid", uuid);
        return "login";
    }

    @PostMapping("/signin")
    @ResponseBody
    public String signin(FormUser formUser, HttpServletRequest request) throws Exception {
        log.info("formUser: {}", formUser);

        // 查询验证码
        String code = (String) captchaStore.get(formUser.getUuid());
        // 清除验证码
        captchaStore.remove(formUser.getUuid());

        // 核验验证码
        if (StringUtils.isEmptyOrWhitespace(code)) {
            throw new Exception("EXPIRED");
        }
        if (StringUtils.isEmptyOrWhitespace(formUser.getCode()) || !formUser.getCode().equalsIgnoreCase(code)) {
            throw new Exception("INVALID");
        }

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(formUser.getUsername(), formUser.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    @RequestMapping(value = "/")
    @ResponseBody
    public String loginSuccess() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return SecurityContextHolder.getContext().getAuthentication().toString();
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

### 用户信息

为了集中焦点在本篇的主题验证码上，避免引入其他复杂性，这里采用内存型用户信息来演示，关于从数据库中获取用户信息，可参考[6-SpringSecurity：数据库存储用户信息](https://blog.csdn.net/u013810234/article/details/111657815)。

``` java
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.withUsername("dev").password(new BCryptPasswordEncoder().encode("123")).authorities("p1", "p2").build();
    }
}
```

### 安全配置

关于 `SpringSecurity` 的配置如下，主要是对资源进行保护，完成权限核验：

``` java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasAuthority("p1")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .formLogin()
                .loginPage("/login");
    }
}
```

### 前端页面

``` html
<form action="signin" method="post">
    <span>用户名</span><input type="text" name="username" /> <br>
    <span>密码</span><input type="password" name="password" /> <br>
    <span>验证码</span><input type="text" name="code" /> <span><img th:src="${img}"></span><br>
    <span>验证码</span><input type="text" name="uuid" th:value="${uuid}" /> <br>

    <input type="submit" value="登录">
</form>s
```

Note: 

1. 获取验证码的请求应该放行，这里 `.anyRequest().permitAll()` 包含了登录页的其他URL。
2. 以上仅实现了 `SpringSecurity` 集成验证码的核心逻辑，实际使用时，密码甚至用户名应该密文传输：前端加密，后端解密，再交由 `SpringSecurity` 进行认证；
3. 前端页面点击验证码刷新可自行实现；

### Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/)
* [图形验证码：EasyCaptcha](https://gitee.com/whvse/EasyCaptcha)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***