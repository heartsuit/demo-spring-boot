## SpringBoot集成Swagger3.0，动态生成接口文档

1. 引入依赖
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>${swagger.version}</version>
</dependency>
```

2. 配置类

```java
@Configuration
@EnableOpenApi
public class SwaggerConfig {
    private static final String VERSION = "1.0.0";

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.heartsuit.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot+Swgger3.0后端服务接口文档")
                .contact(new Contact("Heartsuit", "https://blog.csdn.net/u013810234", "454670286@qq.com"))
                .description("基于Swagger3.0生成的接口文档")
                .termsOfServiceUrl("https://blog.csdn.net/u013810234")
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version(VERSION)
                .build();
    }
}
```

3. 在控制器以及接口上添加注解

```java
@Api(tags = "测试Controller")
@RestController
public class HelloController {
    @GetMapping("hello")
    @ApiOperation("哈喽")
    public String hello() {
        return "Hello SpringBoot with Swagger3.0";
    }
}
```

4. 启动服务，浏览器访问

Note：
- Swagger2.x的访问地址：http://localhost:8080/swagger-ui.html
- Swagger3.0的访问地址：http://localhost:8080/swagger-ui/index.html

5. 控制生成文档的开关

实际中我们的接口文档只会在开发环境下使用，所以一般我们会在生产环境下关闭文档。

- application.yml
```yaml
spring:
  profiles:
    active: dev
```

- application-dev.yml
```yaml
springfox:
  documentation:
    enabled: true
```

- application-prod.yml
```yaml
springfox:
  documentation:
    enabled: false
```