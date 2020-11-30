1. SpringBoot+SpringSecurity 跑起来
继承 WebSecurityConfigurerAdapter，重写两个configure方法，分别做认证配置、拦截配置
采用简单的in-memory 模式演示认证流程与权限配置
采用默认的login、logout页面，以及Whitelabel错误页面
解决报错： There is no PasswordEncoder mapped for the id "null"
废弃了NoOpPasswordEncoder
@Bean
public PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance(); // Deprecated
}
PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
默认采用bcrypt

2. SpringBoot+SpringSecurity 自定义登录页面配置，自定义Error页面配置
自定义登录/退出页面
引入模板引擎
错误页面配置
disable csrf
~~封装接口响应~~

3. SpringBoot+SpringSecurity MySQL数据库

4. CSRF

- 实验1：登录时的CSRF

如无配置，默认开启CSRF，，并且SpringSecurity默认忽略"GET", "HEAD", "TRACE", "OPTIONS"等请求。登录时必须携带_csrf参数，与认证信息一并提交，否则报403。

- 实验2：POST接口CSRF

以原生JavaScript操作DOM元素meta，并发送Ajax POST请求，附带服务端下发的csrftoken信息。

```java
public final class HttpSessionCsrfTokenRepository implements CsrfTokenRepository {
	private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

	private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class
			.getName().concat(".CSRF_TOKEN");

	private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

	private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;
    
    .s..
}    
```

- 实验3：退出时的CSRF

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
