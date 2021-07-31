### 一般做法

由各类接口、异常返回不一致，改造为统一使用Result进行封装；这种方式要求每个接口响应都需要写上Result，比较麻烦。

### 增强做法

- @RestControllerAdvice与ResponseBodyAdvice接口实现正常响应拦截，并统一封装；

- @RestControllerAdvice与@ExceptionHandler实现异常响应拦截，并统一封装；


Note: 核心类包括：

- ResultAdvice.java

```java
@RestControllerAdvice
public class ResultAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof String) {
            return objectMapper.writeValueAsString(Result.success(o));
        }
        if (o instanceof Result) {
            return o;
        }
        return Result.success(o);
    }
}
```


- RestExceptionHandler.java

``` java
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> exception(Exception e) {
        log.error("Global exception: {}", e.getMessage(), e);
        return Result.error(CodeMsg.SERVER_ERROR.getCode(), e.getMessage());
    }
}
```