### 背景

- 对于配置中的密码（DB, MQ, Redis等），甚至账号，在生产环境下存明文，不安全，不专业，不合适。

- 一把插着钥匙的锁，能说它是安全的吗？

### 操作流程

Druid本身提供了加密功能，关于Druid实现对配置项的加密，网络上已经有很多这方面的资料，这里简要描述下步骤。

1. 生成密钥与密文

- 如果计算机上有项目用过druid的，那么在maven的仓库目录下会有druid的jar包。

![2020-05-22-druid-path.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-05-22-druid-path.png)

- 在此打开cmd命令行，键入`java -cp .\druid-1.1.21.jar com.alibaba.druid.filter.config.ConfigTools root > generated.txt`

![2020-05-22-druid-encrypt.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-05-22-druid-encrypt.png)

这里生成的`publicKey`和`password`在接下来要放入配置文件。

2. 修改已有的配置

在已有的明文配置文件中，修改druid密码相关配置。

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    druid:
      url: jdbc:mysql://localhost:3306/boost-admin?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useSSL=false
      username: root
      password: D/pBfX7uKgNrFNtVbvf5pevX5JAcBbzisC/4JK3hTN5Xty3sm8zWtSjd9TwggT/phP8Ob0wg1qZRVolxmd/39g==
      # encrypt config
      filters: config
      connect-properties:
        config.decrypt: true
        config.decrypt.key: MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIYYdcdptMU6n/4wtb7StmX4LFvmlw7+b5KHm7L8C0txn1+iMeXz3FM7emkGkKMuaLd9OazkjgxNpPCDRaNM7ecCAwEAAQ==

mybatis:
  mapperLocations: classpath:mapper/*.xml
  typeAliasesPackage: com.heartsuit.springbootmybatis.oa.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```
上面的修改主要有：

![2020-05-22-druid-config.png](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2020-05-22-druid-config.png)


3. 测试修改后的配置

略（按照上述配置，应一切正常~~）

**Note**: 需要注意的是，druid是用私钥加密，公钥解密的，即如果公钥为所有人可见，那么所有人均可以解密。因此，上述配置若在团队内可见，没什么影响，但是如果配置文件万一被放到了公网上，相当于把钥匙插在锁上，白加密了。。在生产环境下，建议改为如下配置：

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    druid:
      url: jdbc:mysql://localhost:3306/boost-admin?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useSSL=false
      username: root
      password: D/pBfX7uKgNrFNtVbvf5pevX5JAcBbzisC/4JK3hTN5Xty3sm8zWtSjd9TwggT/phP8Ob0wg1qZRVolxmd/39g==
      # encrypt config
      filters: config
      connect-properties:
        config.decrypt: true
        config.decrypt.key: ${spring.datasource.druid.publickey}

mybatis:
  mapperLocations: classpath:mapper/*.xml
  typeAliasesPackage: com.heartsuit.springbootmybatis.oa.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

一行之差：`config.decrypt.key: ${spring.datasource.druid.publickey}`，这里`${spring.datasource.druid.publickey}`可以有两种传入方式：

- 通过服务启动参数

```bash
java -jar xxx.jar --spring.datasource.druid.publickey=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIYYdcdptMU6n/4wtb7StmX4LFvmlw7+b5KHm7L8C0txn1+iMeXz3FM7emkGkKMuaLd9OazkjgxNpPCDRaNM7ecCAwEAAQ==
```
- 通过系统环境变量

这个可以通过Idea IDE传入（开发环境），或者实际的系统环境变量传入（生产环境）。

### Druid的加密与解密

通过`druid`命令行的方式生产密文密码后，可以用`druid`提供的API进行解密，当然，也可以用API的方式来完成加密。

1. 加密

```java
public void encrypt() throws Exception {
    String password = "root";
    String[] keyPair = ConfigTools.genKeyPair(512);
    String privateKey = keyPair[0];
    String publicKey = keyPair[1];
    System.out.println("私    钥： " + privateKey);
    System.out.println("公    钥： " + publicKey);
    String encryptPassword = ConfigTools.encrypt(privateKey, password);
    System.out.println("密文密码： " + encryptPassword);

    /** 输出：
        * 私    钥： MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmsdCBWKd4FRoGJ0+PMtIepI8LWAHd3TysWGKOlfvF8UtXqOv8agodlQ4jcbPH6JoVRcEkgpqteWhj/v7vwc61QIDAQABAkB5ockCTmNfHTXI0hlM0TueBzl/Nw3nFGJ8fviPrPbZqAM6OTZNuA8Uka7AAU5MpbwYbrNcRjXqT5RzaicNrOPBAiEA0BuYwQmRzYLY27xkY99BfLQqwUimR3kwowBUHToV3/0CIQC+ZdNJ71zTW5WDARUz8B8vOBZYfJx25qrCzHbL3DfhuQIhAMwyF9tpeV/uQLyzCMoaONaUrdMDZuyAlGGMI/ydjvM9AiBb650OXNlb0SNlk+hAovTrPxDKt55yaPqYAU55LWBtQQIgYsLxtClktL+ZgVQkGL7Rqa44E7L1TYHl8zyBSbaeYiQ=
        * 公    钥： MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJrHQgVineBUaBidPjzLSHqSPC1gB3d08rFhijpX7xfFLV6jr/GoKHZUOI3Gzx+iaFUXBJIKarXloY/7+78HOtUCAwEAAQ==
        * 密文密码： VFDsVA2tKIonKgNtPHadtBljYkuA1K6PnW8hTy1ZzADbRldd280Z/nbHv5TW9J7JZyK/q411Sg1GE4elxKoYcQ==
        * */
}
```

2. 解密

```java
public void decrypt() throws Exception {
    String encrypted = "VFDsVA2tKIonKgNtPHadtBljYkuA1K6PnW8hTy1ZzADbRldd280Z/nbHv5TW9J7JZyK/q411Sg1GE4elxKoYcQ==";
    String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJrHQgVineBUaBidPjzLSHqSPC1gB3d08rFhijpX7xfFLV6jr/GoKHZUOI3Gzx+iaFUXBJIKarXloY/7+78HOtUCAwEAAQ==";

    String decrypted = ConfigTools.decrypt(publicKey, encrypted);
    System.out.println("解密之后：" + decrypted);
    /** 输出：
    * 解密之后：root
    * */
}
```

### 总结

实现对配置文件敏感数据的加密，网上资源很多，但一定要注意安全性，不可以把公钥共开配置在文件中；还是开头那句话：

> 一把插着钥匙的锁，能说它是安全的吗？

### Source Code: [Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-encrypt/springboot-druid)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***