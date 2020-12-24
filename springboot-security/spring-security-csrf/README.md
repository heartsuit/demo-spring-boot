配合springboot-security 工程测试
1. 引入Thymeleaf，新建index.html

- springboot-security 域名：http://hello:8080
- spring-security-csrf 域名：http://world:8081

实验1：测试Get请求跨域
模拟CSRF Get: <img src="http://hello:8080/security/user/add">

如无配置，SpringSecurity默认开启CSRF，并且SpringSecurity默认忽略"GET", "HEAD", "TRACE", "OPTIONS"等请求：

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

Note: 在Chrome内核浏览器下不生效，在Firefox下测试成功

实验2：测试Post请求跨域

```javascript
<script language="JavaScript">
    function click() {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "http://hello:8080/security/greeting", true);
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
```

使用`Firefox`测试，SpringSecurity默认开启CSRF，POST请求返回403；
在springboot-security工程中关闭csrf: `csrf().disable()`,POST 正常返回，不过由于浏览器同源策略限制，   
