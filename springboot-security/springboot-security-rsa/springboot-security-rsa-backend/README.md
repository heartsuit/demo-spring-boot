## 背景

登录认证几乎是所有互联网应用的必备功能，传统的用户名-密码认证方式依然流行，如何避免用户名、密码这类敏感信息在认证过程中被嗅探、破解？

![2021-09-02-Chart.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-09-02-Chart.jpg)

这里将传统的用户名、密码明文传输方式改为采用 `RSA` 的非对称加密算法密文传输，即使认证请求被网络抓包，只要私钥安全，则认证流程中的用户信息相对安全；

1. 一般是生成`RSA`的密钥对之后，公钥存储在前端或后端（登录时每次请求后端返回公钥）进行加密，私钥存储在后端用于解密；
2. 曾在实际的应用中看到过动态生成密钥对的做法，即公钥-私钥都是动态生成，每次请求都不一样，这与固定公钥-私钥的做法相比，性能上损耗较大，而在安全性上的收益并没有增加多少；因此这里采用固定密钥对的方式进行演示。

## 生成RSA密钥对

主要涉及三条命令：

```bash
# 生成RSA私钥
genrsa -out rsa_private_key.pem 1024

# 把RSA私钥转换成PKCS8格式
pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt

# 生成RSA公钥
rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem
```

* Windows操作系统: Win10

下载安装 `OpenSSL` ：[https://slproweb.com/products/Win32OpenSSL.html](https://slproweb.com/products/Win32OpenSSL.html)

打开 `openssl.exe` 所在目录，我这里是： `D:\Program Files\OpenSSL-Win64\bin` ，执行上述三行命令实现 `RSA` 密钥对生成：

```bash
OpenSSL> genrsa -out rsa_private_key.pem 1024
Generating RSA private key, 1024 bit long modulus (2 primes)
.....................................+++++
................+++++
e is 65537 (0x010001)

OpenSSL> pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt
-----BEGIN PRIVATE KEY-----
MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL/KFpxZ2ZJq4/f8
1oM2LX/aX1llPL6SlFbk5pBw1ESuQDVrcA8T4grdrFoEY6T2mNQAMiuzRKfYkS1l
Qx1C+L0HruqOPhFwDL7rxrDQU+8g/trCv+DQoMAbIcteqgxLQrvMZs1OuJrK0XpG
p4Ca7Wxfuk8HUynjQ9fhXIjWzWTjAgMBAAECgYBMUAARNFszPF77RNqiGQOftOdt
ra+u8KofrTLk1FBSB7e6ycYr6bBuvGeg5dA0Sn7jFDTiWJF/69dQZdN/qC9Kb0OV
jRtXDCSMHe1oRlvDr8tZKn9h9UljJHXrIapXJi5Z1eNQ3DW8ltgJbx/DpQrsSTYJ
JiWWpwfb6e+ub09JEQJBAOt+DAxec2h1Gq43Fc/fJ6hUmVl0VI0d5WkeVHezhutE
gYj29gkHkQin5VIMbXtutB/083vUm+Fxqc5EXdxzYIsCQQDQfb+gNZgBzeNhF/j5
IdqW68PpSOmWj2z9sVvAktSS9VzTt46haBvnjzIbES+uzJXoW0LI0H1zDlbvbtRV
HQAJAkEAz+kQMBdvowjIzok5y7ZEqBxQ66aGQ7TiZ2Vsw+YPt0VbbBZF8IDqro61
KzRnsLNzekdkdK6oFWmptr+rcse2swJARN10QSfSqK3n7/cqHqgm+nivgku6FCgV
uQovI0Gcg1oWKjxUGU45AVhUFYqstFERJumV+pybAzj2UCnMarykeQJAAkXb5Z7A
sb7wmLCDMoyfzJCn54k1VDEvGVcrn4SiME53wEyGnrYkyg8R84hO7rHLOnwz0PtZ
iLWuHpqd2OovmA==
-----END PRIVATE KEY-----

OpenSSL> rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem
writing RSA key
```

* Linux操作系统：CentOS7

同样，执行上述三行命令实现 `RSA` 密钥对生成：

![2021-09-02-RSALinux.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-09-02-RSALinux.jpg)

Note：
1. 后续编码实现时，使用Windows上生成的秘钥进行演示；
2. 公钥、私钥用的是下图中**红色椭圆**标注出来的内容。

![2021-09-02-GererateRSA.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-09-02-GererateRSA.jpg)

```
公钥：MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/yhacWdmSauP3/NaDNi1/2l9ZZTy+kpRW5OaQcNRErkA1a3APE+IK3axaBGOk9pjUADIrs0Sn2JEtZUMdQvi9B67qjj4RcAy+68aw0FPvIP7awr/g0KDAGyHLXqoMS0K7zGbNTriaytF6RqeAmu1sX7pPB1Mp40PX4VyI1s1k4wIDAQAB
私钥：MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL/KFpxZ2ZJq4/f81oM2LX/aX1llPL6SlFbk5pBw1ESuQDVrcA8T4grdrFoEY6T2mNQAMiuzRKfYkS1lQx1C+L0HruqOPhFwDL7rxrDQU+8g/trCv+DQoMAbIcteqgxLQrvMZs1OuJrK0XpGp4Ca7Wxfuk8HUynjQ9fhXIjWzWTjAgMBAAECgYBMUAARNFszPF77RNqiGQOftOdtra+u8KofrTLk1FBSB7e6ycYr6bBuvGeg5dA0Sn7jFDTiWJF/69dQZdN/qC9Kb0OVjRtXDCSMHe1oRlvDr8tZKn9h9UljJHXrIapXJi5Z1eNQ3DW8ltgJbx/DpQrsSTYJJiWWpwfb6e+ub09JEQJBAOt+DAxec2h1Gq43Fc/fJ6hUmVl0VI0d5WkeVHezhutEgYj29gkHkQin5VIMbXtutB/083vUm+Fxqc5EXdxzYIsCQQDQfb+gNZgBzeNhF/j5IdqW68PpSOmWj2z9sVvAktSS9VzTt46haBvnjzIbES+uzJXoW0LI0H1zDlbvbtRVHQAJAkEAz+kQMBdvowjIzok5y7ZEqBxQ66aGQ7TiZ2Vsw+YPt0VbbBZF8IDqro61KzRnsLNzekdkdK6oFWmptr+rcse2swJARN10QSfSqK3n7/cqHqgm+nivgku6FCgVuQovI0Gcg1oWKjxUGU45AVhUFYqstFERJumV+pybAzj2UCnMarykeQJAAkXb5Z7Asb7wmLCDMoyfzJCn54k1VDEvGVcrn4SiME53wEyGnrYkyg8R84hO7rHLOnwz0PtZiLWuHpqd2OovmA==
```

## 后端服务

基于 `SpringBoot` , `SpringSecurity` 实现用户认证功能。

### 项目依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### SpringSecurity配置

注意放行认证接口，否则报错：403。

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // turn off csrf, or will be 403 forbidden
                .csrf().disable();
    }
}
```

### 用户信息配置

为了集中焦点在本篇的用户名-密码加密传输上，避免引入其他复杂性，这里采用内存型用户信息来演示，关于从数据库中获取用户信息，可参考[6-SpringSecurity：数据库存储用户信息](https://blog.csdn.net/u013810234/article/details/111657815)。

```java
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.withUsername("dev").password(new BCryptPasswordEncoder().encode("123")).authorities("p1", "p2").build();
    }
}
```

### 认证接口

这里将私钥配置在 `applicaiton.yml` 中。

```java
@RestController
@RequestMapping("auth")
@Slf4j
public class LoginController {
    @Value("${rsa.private_key}")
    private String privateKey;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    public String login(@RequestBody FormUser formUser, HttpServletRequest request) {
        log.info("formUser encrypted: {}", formUser);

        // 用户信息RSA私钥解密，方法一：自定义工具类：RSAEncrypt
//        String username = RSAEncrypt.decrypt(formUser.getUsername(), privateKey);
//        String password = RSAEncrypt.decrypt(formUser.getPassword(), privateKey);
//        log.info("Userinfo decrypted: {}, {}", username, password);

        // 用户信息RSA私钥解密，方法二：使用hutool中的工具类进行解密
        RSA rsa = new RSA(privateKey, null);
        String username = new String(rsa.decrypt(formUser.getUsername(), KeyType.PrivateKey));
        String password = new String(rsa.decrypt(formUser.getPassword(), KeyType.PrivateKey));
        log.info("Userinfo decrypted: {}, {}", username, password);

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
```

### 自定义工具类进行解密

```xml
<dependency>
  <groupId>commons-codec</groupId>
  <artifactId>commons-codec</artifactId>
  <version>1.12</version>
</dependency>
```

```java
public class RSAEncrypt {
    /**
     * RSA公钥加密
     * @param str       待加密字符串
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encrypt(String str, String publicKey) {
        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA私钥解密
     * @param str        已加密字符串
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decrypt(String str, String privateKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 使用hutool中的工具类进行解密

```xml
<dependency>
  <groupId>cn.hutool</groupId>
  <artifactId>hutool-all</artifactId>
  <version>5.0.6</version>
</dependency>	
```

## 前端工程

基于 `Vue3.0` , `axios` 实现极简登录页面。

Note: 

1. 前提需要有 `Node.js` 环境，可使用 `nvm` 进行 `Node.js` 的多版本管理；可参考[https://heartsuit.blog.csdn.net/article/details/116665356](https://heartsuit.blog.csdn.net/article/details/116665356)
2. `npm install <package>`默认会在依赖安装完成后将其写入`package.json`，因此安装依赖的命令都未附加`save`参数。

```bash
$ node -v
v12.16.1
```

### 安装vue-cli并创建项目

```bash
npm install -g @vue/cli
vue --version
vue create hello-world
```

刚开始的 `package.json` 依赖是这样：

```json
  "dependencies": {
    "core-js": "^3.6.5",
    "vue": "^3.0.0"
  },
```

### 集成Axios

* 安装依赖

```bash
npm install axios
```

此时， `package.json` 的依赖变为：

```json
  "dependencies": {
    "axios": "^0.21.1",
    "core-js": "^3.6.5",
    "vue": "^3.0.0"
  },
```

* 按需引入

在需要使用axios的组件中引入 `import axios from "axios"; `

### 集成jsencrypt

此时， `package.json` 的依赖变为：

```json
  "dependencies": {
    "axios": "^0.21.1",
    "core-js": "^3.6.5",
    "jsencrypt": "^3.2.1",
    "vue": "^3.0.0"
  },
```

* 按需引入

在需要使用JSEncrypt的组件中引入 `import JSEncrypt from "jsencrypt"; `

### 最终的前端登录组件代码

```vue
<template>
  <div>
    <span>用户名</span><input type="text" v-model="user.username" />
    <span>密码</span><input type="text" v-model="user.password" />
    <input type="submit" v-on:click="login" value="登录" />
  </div>
</template>
<script>
import { defineComponent } from "vue";
import axios from "axios";
import JSEncrypt from "jsencrypt";

export default defineComponent({
  name: "RSADemo",
  setup() {},
  data() {
    return {
      user: { username: "dev", password: 123 },
      publicKey: `MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/yhacWdmSauP3/NaDNi1/2l9Z
ZTy+kpRW5OaQcNRErkA1a3APE+IK3axaBGOk9pjUADIrs0Sn2JEtZUMdQvi9B67q
jj4RcAy+68aw0FPvIP7awr/g0KDAGyHLXqoMS0K7zGbNTriaytF6RqeAmu1sX7pP
B1Mp40PX4VyI1s1k4wIDAQAB`,
    };
  },
  mounted() {
    this.login();
  },
  methods: {
    login: function () {
      let userinfo = {
        username: this.encrypt(this.user.username),
        password: this.encrypt(this.user.password),
      };

      axios.post("http://localhost:8000/auth/login", userinfo).then(
        function (res) {
          if (res.status == 200) {
            console.log(res.data);
          } else {
            console.error(res);
          }
        },
        function (res) {
          console.error(res);
        }
      );
    },
    encrypt: function (str) {
      let jsEncrypt = new JSEncrypt();
      // 设置加密公钥，一般通过后端接口获取，这里写在前端代码中
      jsEncrypt.setPublicKey(this.publicKey);
      let encrypted = jsEncrypt.encrypt(str.toString());
      return encrypted;
    },
  },
});
</script>
```

## RSA加密传输效果

![2021-09-02-AuthEncrypted.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-09-02-AuthEncrypted.jpg)

![2021-09-02-AuthDecrypted.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2021-09-02-AuthDecrypted.jpg)

## 可能遇到的问题

* 开发环境跨域

方法一：通过开发环境（生产环境可通过Nginx实现）的代理服务进行请求转发，新建 `vue.config.js` 文件，内容如下：

```js
module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8000/',
                changeOrigin: true,
                ws: true,
                secure: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        }
    }
};
```

方法二：因为后端服务是我们自己开发的，所以可以在后端进行CORS配置，允许跨域

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOriginPatterns("*").
                        allowedMethods("*").
                        allowedHeaders("*").
                        allowCredentials(true).
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
            }
        };
    }
}
```

## 附：代码生成RSA密钥对

当然，除了使用Windows、Linux上的openssl工具生成密钥对之外，我们也可以使用代码来直接生成。

```xml
<dependency>
  <groupId>org.bouncycastle</groupId>
  <artifactId>bcprov-jdk15on</artifactId>
  <version>1.64</version>
</dependency>
```

```java
public class RSAEncrypt {
    private static final KeyPair keyPair = genKeyPair() ;
    private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider = null;

    public static synchronized org.bouncycastle.jce.provider.BouncyCastleProvider getInstance() {
        if (bouncyCastleProvider == null) {
            bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        }
        return bouncyCastleProvider;
    }

    /**
     * 随机生成密钥对
     */
    public static KeyPair  genKeyPair()  {
        try {
//            Provider provider =new org.bouncycastle.jce.provider.BouncyCastleProvider();
//            Security.addProvider(DEFAULT_PROVIDER);
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getInstance());
            generator.initialize(1024,random);
            return generator.generateKeyPair();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取公钥字符串（base64字符串）
     * @return
     */
    public static String generateBase64PublicKey() {
        PublicKey  publicKey = (RSAPublicKey) keyPair.getPublic();
        return new String(Base64.encodeBase64(publicKey.getEncoded()));
    }
    /**
     * 获取私钥字符串（base64字符串）
     * @return
     */
    public static String generateBase64PrivateKey() {
        PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到私钥字符串
        return new String(Base64.encodeBase64((privateKey.getEncoded())));
    }
    ...
}
```

## Reference

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security)
* [https://blog.csdn.net/aexlinda/article/details/37693167](https://blog.csdn.net/aexlinda/article/details/37693167)
---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***
