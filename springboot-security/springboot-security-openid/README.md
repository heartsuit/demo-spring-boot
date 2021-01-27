### 背景

本系列教程，是作为团队内部的培训资料准备的。主要以实验的方式来体验 `SpringSecurity` 的各项Feature。
 
目前 `SpringSecurity` 新版本除了实现对 `OAuth2.0` 的支持外，还支持 `OpenID` 及 `SAML` 。

> 果然，Spring Security不仅是一个功能强大且可高度自定义的身份验证和访问控制框架，它还是保护基于Spring的应用程序的事实标准。

`SpringSecurity` 本身提供了 `GOOGLE`  `GITHUB`  `FACEBOOK`  `OKTA` 的 `OAuth2.0` 接入支持，具体源码在枚举类 `CommonOAuth2Provider` 中。[上一篇文章：12-SpringSecurity：通过OAuth2集成Github登录](https://blog.csdn.net/u013810234/article/details/112911491)实现了 `Github` 的 `OAuth2.0` 接入，这次实现对 `OIDC` 提供方（eg: `Okta` , `Keycloak` , `Authing` ）的集成， 这里主要使用由 `Keycloak` 提供的 `OIDC` (OpenID Connect) 服务，实现 `Spring Security 5` 集成 `OIDC` 单点登录。。

* `OIDC` (OpenID Connect)是什么？

    OpenID Connect 1.0 is a simple identity layer on top of the OAuth 2.0 protocol. It allows Clients to verify the identity of the End-User based on the authentication performed by an Authorization Server, as well as to obtain basic profile information about the End-User in an interoperable and REST-like manner.

    OpenID Connect allows clients of all types, including Web-based, mobile, and JavaScript clients, to request and receive information about authenticated sessions and end-users. The specification suite is extensible, allowing participants to use optional features such as encryption of identity data, discovery of OpenID Providers, and session management, when it makes sense for them.

即 `OAuth2.0` 是一种授权协议， `OIDC ` 是一个基于 `OAuth2.0` 授权协议的身份认证层： 

> `(Identity, Authentication) + OAuth2.0 = OpenID Connect` 

* `Keycloak` 是什么？

    Open Source Identity and Access Management for Modern Applications and Services。

即： `Keycloak` 是一种面向现代应用和服务的开源 `IAM` （身份识别与访问管理：Identity and Access Management）解决方案。我们可以使用 `Keycloak` 来搭建一个属于自己的 `OIDC` 认证-授权服务器。

### 实验0：Keycloak使用

`Keycloak` 的使用可参考[Keycloak官方文档](https://www.keycloak.org/getting-started/getting-started-zip)，官方文档简洁明了，没有一句废话。

主要步骤是这样的：下载安装-启动-创建admin用户-登录admin控制台-创建域-创建用户-创建应用。

我创建了一个 `admin` 用户，登录之后是下图这样，可见 `Keycloak` 提供了全面的认证、授权服务，功能很强大；然后创建了一个 `Heartsuit` 域。

![2021-01-27-Keycloak.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-Keycloak.png)

在 `Heartsuit` 域下创建了一个用户： `auth` 

![2021-01-27-ConfigUser.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-ConfigUser.png)

在 `Heartsuit` 域下创建了一个应用： `springsecurity` 

![2021-01-27-ConfigClient.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-ConfigClient.png)

需要注意的是，由于我们后续使用授权码模式进行登录，所以应用配置中：

* `Access Type` 选择 `confidential` ；然后在 `Credential` 或者 `Installation` 可以看到 `Secret` ；
* `Valid Redirect URIs` 配置为： `http://localhost:8000/login/oauth2/code/keycloak` ；

![2021-01-27-ConfigSecret.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-ConfigSecret.png)

![2021-01-27-ConfigInstall.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-ConfigInstall.png)

至此，我们得到了一个应用：

``` json
{
  "realm": "heartsuit",
  "auth-server-url": "http://localhost:8080/auth/",
  "ssl-required": "external",
  "resource": "springsecurity",
  "credentials": {
    "secret": "6b532289-4c11-4e62-acc0-5c67e13e4736"
  },
  "confidential-port": 0
}
```

### 实验1：Keycloak登录

![2021-01-27-SpringSecurityOpenID.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-SpringSecurityOpenID.png)

（1） 注册应用

在前面注册的应用，生成了 `client-id` （resource）， `client-secret` （secret）。

（2） 配置 `application.yml` 

``` yml
spring:
  security:
    oauth2:
      client:
        provider:
          keycloak:
            issuer-uri: http://localhost:8080/auth/realms/heartsuit
        registration:
          keycloak:
            client-id: springsecurity
            client-secret: 6b532289-4c11-4e62-acc0-5c67e13e4736
            clientName: Keycloak
          scope:
            - openid
            - profile
            - email

server:
  port: 8000
```

（3） 启动应用

为了看到登录成功后的效果，这里增加一个 `Controller` ；然后运行应用。

``` java
    @GetMapping(value = "/")
    public String index(Principal principal) {
        return "Welcome " + principal;
    }
```

在浏览器键入： `http://localhost:8000/login` ，返回一个页面，其中包含了 `Keycloak` 登录链接：

![2021-01-27-Login.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-Login.png)

点击 `Keycloak` 登录链接，会自动跳转至 我们创建的 `Keycloak` 服务认证页：

![2021-01-27-LoginForm.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-LoginForm.png)

输入在 `Heartsuit` 域下创建的用户： `auth` ，会进入我们之前配置的Home页，显示用户信息。

Note: 如果我们直接在浏览器中输入 `http://localhost:8000` 则会自动跳转到
`http://localhost:8080/auth/realms/heartsuit/protocol/openid-connect/auth?response_type=code&client_id=springsecurity&state=Gd5Xj0PyueFcDtoQ6zC6w2wSVc4XjAbAFn8q_uu0qes%3D&redirect_uri=http://localhost:8000/login/oauth2/code/keycloak` 链接。

可通过链接退出： `http://localhost:8000/logout` 

借助 `SpringSecurity` 对 `OpenID` 的支持，我们几乎不用写什么代码就实现了 `Keycloak` 登录集成。下面简单了解下登录成功后的 `Registration` ， `AccessToken` 。

### 实验2：查看Keycloak在我们应用中的注册信息

为了方便调试或查看 `registration` ，这里新增一个接口端点：

``` java
    @GetMapping(value = "/user/reg")
    public String registration() {
        ClientRegistration keycloakRegistration = this.clientRegistrationRepository.findByRegistrationId("keycloak");
        log.info(keycloakRegistration.toString());
        return keycloakRegistration.toString();
    }
```

访问之后会返回 `registration` 信息，其中包含了 `clientId` ， `clientSecret` ， `authorizationGrantType` ， `redirectUri` ， `scopes` 等。

![2021-01-27-KeycloakRegistration.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-KeycloakRegistration.png)

### 实验3：查看获取到的AccessToken

``` java
    @GetMapping(value = "/user/token")
    public OAuth2AccessToken accessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }
```

![2021-01-27-OpenIDToken.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-OpenIDToken.png)

显然，这里的 `tokenValue` 是一个 `JWT` 字符串，我们到 `https://jwt.io` 解析一下：

![2021-01-27-OpenIDJWT.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-OpenIDJWT.png)

### 实验4：通过AccessToken请求Keycloak的用户信息接口

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
                throw new IllegalStateException("Can't access the Keycloak API without an access token");
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
    public Keycloak keycloak(OAuth2AuthorizedClientService clientService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        String userInfoEndpointUri = null;
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            if (clientRegistrationId.equals("keycloak")) {
                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
                if (client != null) {
                    accessToken = client.getAccessToken().getTokenValue();
                    userInfoEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
                }
                log.info(accessToken);
                log.info(userInfoEndpointUri);
            }
        }
        return new Keycloak(accessToken, userInfoEndpointUri);
    }
}

public class Keycloak extends ApiBinding {
    private String userInfoEndpointUri;

    public Keycloak(String accessToken, String userInfoEndpointUri) {
        super(accessToken);
        this.userInfoEndpointUri = userInfoEndpointUri;
    }

    public String getProfile() {
        return restTemplate.getForObject(userInfoEndpointUri, String.class);
    }
}
```

在 `Controller` 中新增接口：通过 `AccessToken` 获取 `keycloak` 用户信息：

``` java
    @GetMapping(value = "/user/info")
    public String info() {
        String profile = keycloak.getProfile();
        log.info(keycloak.getProfile());
        return profile;
    }
```

![2021-01-27-UserInfo.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-UserInfo.png)

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
    Keycloak keycloak;

    @GetMapping(value = "/")
    public String index(Principal principal) {
        return "Welcome " + principal;
    }

    @GetMapping(value = "/user/reg")
    public String registration() {
        ClientRegistration keycloakRegistration = this.clientRegistrationRepository.findByRegistrationId("keycloak");
        log.info(keycloakRegistration.toString());
        return keycloakRegistration.toString();
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
        String profile = keycloak.getProfile();
        log.info(keycloak.getProfile());
        return profile;
    }
}
```

最后，有两个端点需要说明一下，这在我们对接一些支持 `OpenID` 登录其他第三方应用时要用到：

1. `http://localhost:8080/auth/realms/heartsuit/`， 我们在 `application.yml` 配置中使用过；
2. `http://localhost:8080/auth/realms/heartsuit/.well-known/openid-configuration`，第三方应用会自动请求该链接，这在 `SpringSecurity` 官方文档中也有说明。

![2021-01-27-Realm.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-Realm.png)

![2021-01-27-WellKnown.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-01-27-WellKnown.png)

### Source Code

* [Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-openid)

### Reference

* [SpringSecurity官方文档](https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#webflux-oauth2-login-openid-provider-configuration)
* [OpenID官网](https://openid.net)
* [OAuth2 Login 教程](https://github.com/spring-projects/spring-security/tree/5.4.1/samples/boot/oauth2login)
* [Keycloak官方文档](https://www.keycloak.org/getting-started/getting-started-zip)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***