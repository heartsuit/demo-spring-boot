# springboot-admin 的客户端

## SpringBoot2开启Actuator端点监控

[https://heartsuit.blog.csdn.net/article/details/93764101](https://heartsuit.blog.csdn.net/article/details/93764101)

## SpringBoot Admin 实现Actuator端点可视化监控

[https://heartsuit.blog.csdn.net/article/details/93892607](https://heartsuit.blog.csdn.net/article/details/93892607)

## SpringBoot Admin 实现Actuator端点可视化监控（开启认证）

[https://heartsuit.blog.csdn.net/article/details/94116072](https://heartsuit.blog.csdn.net/article/details/94116072)

## 更新了SpringBoot版本， SpringBootAdmin的Httptrace不见了

[https://heartsuit.blog.csdn.net/article/details/110097201](https://heartsuit.blog.csdn.net/article/details/110097201)

## MyBatisPlus的SQL语句日志写入文件，并在SpringBootAdmin动态显示

```yaml
logging:
  pattern:
    file: '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID}){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx'
  file:
    name: books.log
    path: D:\Java\IdeaProjects\demo-springboot\springboot-admin\springboot-admin-client
  level:
    com.heartsuit.client.*: INFO
    com.baomidou.mybatisplus: DEBUG
    com.heartsuit.client.mapper: DEBUG
```