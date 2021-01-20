### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。
 
目前 `SpringSecurity` 新版本已实现了对 `OAuth2.0` 的支持。 `OAuth2.0` 是一个开放的授权标准，允许用户授权第三方网站访问他们存储在另外的服务提供者上的信息，而不需要将用户凭据（用户名、密码）提供给第三方网站。
 
`SpringSecurity` 本身提供了 `GOOGLE` `GITHUB` `FACEBOOK` `OKTA` 的 `OAuth2.0` 接入支持，具体源码在枚举类 `CommonOAuth2Provider` 中。这里仅对 `SpringSecurity` 中的 `OAuth2.0` 这一新特性做个体验：将我们的应用（第三方网站）作为一个 `OAuth2.0` 的客户端来集成 `Github` 登录（ `OAuth2.0` 服务端），并实现对`Github`资源（资源服务器）的访问。

### 实验0：OAuth2.0客户端的Github登录

（1） 注册应用

在Github注册一个应用，生成 `client-id` ， `client-secret` 。

![2021-01-20-GithubApp.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-GithubApp.png)

注意这里的Home页： `http://localhost:8080` ，以及回调地址： `http://localhost:8080/login/oauth2/code/github` 

（2） 配置 `application.yml` 

``` yml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: <your-client-id>
            client-secret: <your-client-secret>
```

（3） 启动应用

为了看到登录成功后的效果，这里增加一个 `Controller` ；然后运行应用。

``` java
    @GetMapping(value = "/")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }
```

在浏览器键入： `http://localhost:8080/login` ，返回一个页面，其中包含了 `Github` 登录链接：

![2021-01-20-Login.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-Login.png)

点击 `Github` 登录链接，会自动跳转至 `Github` 的认证页：

![2021-01-20-GithubLogin.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-GithubLogin.png)

输入Github的账号、密码，会进入我们之前配置的Home页：

![2021-01-20-OAuth2Github.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-OAuth2Github.png)

可通过链接退出： `http://localhost:8080/logout` 

借助 `SpringSecurity` 对 `OAuth2.0` 的支持，我们几乎不用写什么代码就实现了 `Github` 登录集成。下面简单了解下登录成功后的 `Registration` ， `AccessToken` 。

### 实验1：查看Github在我们应用中的注册信息

为了方便调试或查看 `registration` ，这里新增一个接口端点：

``` java
    @GetMapping(value = "/user/reg")
    public String registration() {
        ClientRegistration githubRegistration = this.clientRegistrationRepository.findByRegistrationId("github");
        log.info(githubRegistration.toString());
        return githubRegistration.toString();
    }
```

访问之后会返回 `registration` 信息，其中包含了 `clientId` ， `clientSecret` ， `authorizationGrantType` ， `redirectUri` ， `scopes` 等。

![2021-01-20-GithubRegistration.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-GithubRegistration.png)

### 实验2：查看获取到的AccessToken

``` java
    @GetMapping(value = "/user/token")
    public OAuth2AccessToken accessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }
```

![2021-01-20-Token.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-Token.png)

Note: 这里的 `issuedAt` ， `expiresAt` 着实诡异，仅差了一秒，是`Github`授权服务问题？还没细看是什么原因。。

### 实验3：通过AccessToken请求Github的API

定义抽象 `API` 绑定类，通过拦截器将获取到的 `AccessToken` 设置到后续请求头中，通过 `RestTemplate` 实现对 `API` 的请求：

``` java
public abstract class ApiBinding {
    protected RestTemplate restTemplate;

    public ApiBinding(String accessToken) {
        this.restTemplate = new RestTemplate();
        if (accessToken != null) {
            this.restTemplate.getInterceptors().add(getBearerTokenInterceptor(accessToken));
        } else {
            this.restTemplate.getInterceptors().add(getNoTokenInterceptor());
        }
    }

    private ClientHttpRequestInterceptor getBearerTokenInterceptor(String accessToken) {
        return new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
                request.getHeaders().add("Authorization", "Bearer " + accessToken);
                return execution.execute(request, bytes);
            }
        };
    }

    private ClientHttpRequestInterceptor getNoTokenInterceptor() {
        return new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
                throw new IllegalStateException("Can't access the Github API without an access token");
            }
        };
    }
}
```

将获取 `AccessToken` 的过程进行封装：

``` java
@Configuration
@Slf4j
public class SocialConfig {
    @Bean
    @RequestScope
    public Github github(OAuth2AuthorizedClientService clientService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            if (clientRegistrationId.equals("github")) {
                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
                if (client != null) {
                    accessToken = client.getAccessToken().getTokenValue();
                }
                log.info(accessToken);
            }
        }
        return new Github(accessToken);
    }
}

public class Github extends ApiBinding {
    private static final String BASE_URL = "https://api.github.com";

    public Github(String accessToken) {
        super(accessToken);
    }
    public String getProfile() {
        return restTemplate.getForObject(BASE_URL + "/user", String.class);
    }
}
```

在 `Controller` 中新增接口：通过 `AccessToken` 获取 `Github` 用户信息：

``` java
    @GetMapping(value = "/user/info")
    public String info() {
        String profile = github.getProfile();
        log.info(github.getProfile());
        return profile;
    }
```

![2021-01-20-UserInfo.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-UserInfo.png)

Note：两个接口的区别：https://api.github.com/users/heartsuit（无需认证），https://api.github.com/user（需要认证）

![2021-01-20-APICompare.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-20-APICompare.png)

`Controller` 的完整代码：

``` java
@RestController
@Slf4j
public class HelloController {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    Github github;

    @GetMapping(value = "/")
    public String index() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return "Welcome " + SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping(value = "/user/reg")
    public String registration() {
        ClientRegistration githubRegistration = this.clientRegistrationRepository.findByRegistrationId("github");
        log.info(githubRegistration.toString());
        return githubRegistration.toString();
    }

    @GetMapping(value = "/user/token")
    public OAuth2AccessToken accessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }

    @GetMapping(value = "/user/info")
    public String info() {
        String profile = github.getProfile();
        log.info(github.getProfile());
        return profile;
    }
}
```

### Source Code

* [Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-oauth2)

### Reference

* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#oauth2)
* [OAuth2 Login 教程](https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github)
* [Github API文档](https://docs.github.com/en/rest/reference/users#get-the-authenticated-user)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***
