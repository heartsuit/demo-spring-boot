### 背景

- 对于配置中的密码（DB, MQ, Redis等），甚至账号，在生产环境下存明文，不安全，不专业，不合适。

- 一把插着钥匙的锁，能说它是安全的吗？

### 操作流程

关于`Jasypt`实现对配置项的加密，网络上已经有很多这方面的资料，这里简要描述下步骤。

1. 引入依赖

```xml
<dependency>
  <groupId>com.github.ulisesbocchio</groupId>
  <artifactId>jasypt-spring-boot-starter</artifactId>
  <version>1.18</version>
</dependency>
```

2. 生成密文

- 如果计算机上有项目用过`Jasypt`的，那么在maven的仓库目录下会有`Jasypt`的jar包。如果本地仓库没有，先下载jar包。在jar包所在目录下打开cmd命令行，键入`java -cp jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="root" password=you-guess algorithm=PBEWithMD5AndDES`

![2020-05-23-jasypt-encypt.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-05-23-jasypt-encypt.png)

最下面输出的`qN66aPx0SrcFulrPfmMXOw==`是密文，在接下来要放入配置文件。

2. 修改已有的配置

在已有的明文配置文件中，修改Jasypt密码相关配置。

```yml
spring:
  datasource:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mp?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useSSL=false
    username: root
    password: ENC(qN66aPx0SrcFulrPfmMXOw==)

# 配置日志
logging:
  level:
    root: info
    com.heartsuit.dao: trace
  pattern:
    console: '%p%m%n'

# 加密密钥
jasypt:
  encryptor:
    password: you-guess
```

上面的修改主要有：

![2020-05-23-jasypt-config.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-05-23-jasypt-config.png)

Note: 生成的密文要用ENC()包起来，这是`Jasypt`的要求。

3. 测试修改后的配置

略（按照上述配置，应一切正常~~）

**Note**: 需要注意的是，用于生成加密后的密码的密钥不可放在配置文件或者代码中，加密密钥`jasypt.encryptor.password=you-guess`可以对密文解密。因此，上述配置若在团队内可见，没什么影响，但是如果配置文件万一被放到了公网上，相当于把钥匙插在锁上，白加密了。。在生产环境下，建议去掉加密密钥配置：

```yml
spring:
  datasource:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mp?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useSSL=false
    username: root
    password: ENC(qN66aPx0SrcFulrPfmMXOw==)

# 配置日志
logging:
  level:
    root: info
    com.heartsuit.dao: trace
  pattern:
    console: '%p%m%n'
```

仅去掉了：`jasypt.encryptor.password=you-guess`，这里`jasypt.encryptor.password=you-guess`可以有两种传入方式：

- 通过服务启动参数

```bash
java -jar xxx.jar --jasypt.encryptor.password=you-guess
```

- 通过系统环境变量

这个可以通过Idea IDE传入（开发环境），或者实际的系统环境变量传入（生产环境）。

### Jasypt的加密与解密

通过`Jasypt`命令行的方式生产密文密码后，可以用`Jasypt`提供的API进行解密，当然，也可以用API的方式来完成加密。

- 加密与解密

```java
@Component
public class StringEncryptDecrypt {

    @Autowired
    StringEncryptor encryptor;

    public String encrypt(String plainText) {
        // Encrypt
        String encrypted = encryptor.encrypt(plainText);
        System.out.println("Encrypted: " + encrypted);
        return encrypted;
    }

    public String decrypt(String encrypted) {
        // Decrypt
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
}
```

### 总结

实现对配置文件敏感数据的加密，网上资源很多，但一定要注意安全性，不可以把公钥共开配置在文件中；还是开头那句话：

> 一把插着钥匙的锁，能说它是安全的吗？

### Source Code: [Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-encrypt/springboot-jasypt)


### Reference: [https://github.com/ulisesbocchio/jasypt-spring-boot](https://github.com/ulisesbocchio/jasypt-spring-boot)
---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***