## 背景

国密算法是我国自主研发创新的一套数据加密处理系列算法，包括SM1, SM2, SM3, SM4, SM7, SM9, 祖冲之密码算法等。从SM1-SM4分别实现了对称、非对称、摘要等算法功能，完成身份认证和数据加解密等功能。我们这里使用的 `SM2` 非对称加密算法，对标替代的是大名鼎鼎的 `RSA` 算法。对于使用过 `RSA` 算法的同学， `SM2` 的用法基本类似，如果您想了解前后端分离项目中使用 `SpringSecurity` 与 `RSA` 算法对用户信息的加密传输，可以参考[14-SpringSecurity：前后端分离项目中用户名与密码通过RSA加密传输](https://heartsuit.blog.csdn.net/article/details/120055728)。

这篇文章依然基于一个简单的前后端分离项目，将以前的 `RSA` 替换为 `SM2` 即可，除此之外，还记录了使用 `OpenSSL` ， `SM2` 以及 `sm-crypto` 时遇到的问题，文末有源码地址。

* 系统信息

```bash
[root@hadoop1 local]# uname -a
Linux hadoop1 3.10.0-1127.el7.x86_64 #1 SMP Tue Mar 31 23:36:51 UTC 2020 x86_64 x86_64 x86_64 GNU/Linux
[root@hadoop1 local]# cat /proc/version
Linux version 3.10.0-1127.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-39) (GCC) ) #1 SMP Tue Mar 31 23:36:51 UTC 2020
[root@hadoop1 local]# cat /etc/redhat-release
CentOS Linux release 7.8.2003 (Core)
```

* 配置信息

```
内存：4G
处理器：2*2
硬盘：100G
```

## SM2秘钥对从哪里来？

在通过 `SpringBoot` 集成 `SM2` 之前，我们要解决的第一个问题是，**秘钥对从哪里来**？我们有以下几种方式来得到 `SM2` 的秘钥对。

1. 使用OpenSSL命令行工具；
2. 使用HuTool工具类（见文末源码中的/sm2接口）；
3. 使用在线工具（见文末链接）；

这里主要使用 `OpenSSL` 命令行工具来生成 `SM2` 的秘钥对，需要注意的是，目前的 `Centos 7` 一般搭配的版本是 `openssl 1.0.2` ，如果要使用 `OpenSSL` 生成的话，需要先升级到 `openssl 1.1.1` 以上，具体的升级方法见文末。与生成 `RSA` 秘钥对类似，我们同样需要用到以下三条命令。

```bash
# 生成SM2私钥
openssl ecparam -genkey -name SM2 -out sm2_private_key.key
# 把SM2私钥转换成PKCS8格式
openssl pkcs8 -topk8 -inform PEM -in sm2_private_key.key -outform PEM -nocrypt
# 使用原始私钥生成SM2公钥
openssl ec -in sm2_private_key.key -pubout -out sm2_public_key.key
```

```bash
[root@hadoop1 ~]# openssl ecparam -genkey -name SM2 -out sm2_private_key.key
[root@hadoop1 ~]# openssl pkcs8 -topk8 -inform PEM -in sm2_private_key.key -outform PEM -nocrypt
-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQgHXugwQeFgF6ELKVH
l28evMOM/Cc2H/EGwv0wcIVYkUShRANCAASrr2gtqvS+u5A1Y7ywDh+6LnxYzum9
sypgJ1bj0S+LyxQJCqg1jE7i/i4LADFJLvO1HY1LQW+6cIM2rLWXzQQG
-----END PRIVATE KEY-----
[root@hadoop1 ~]# openssl ec -in sm2_private_key.key -pubout -out sm2_public_key.key
read EC key
writing EC key
```

这时，得到的**秘钥对**如下：
* 公钥：

```
MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p
vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==
```

* 私钥：

```
MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQgHXugwQeFgF6ELKVH
l28evMOM/Cc2H/EGwv0wcIVYkUShRANCAASrr2gtqvS+u5A1Y7ywDh+6LnxYzum9
sypgJ1bj0S+LyxQJCqg1jE7i/i4LADFJLvO1HY1LQW+6cIM2rLWXzQQG
```

![2022-10-07-Kubernetes.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-10-07-Kubernetes.jpg)

## SpringBoot后端服务如何集成SM2？

### 添加依赖

在pom.xml中添加依赖。

```xml
		<!--SM2相关-->
		<dependency>
			<groupId>org.bouncycastle</groupId>
			<artifactId>bcprov-jdk15on</artifactId>
			<version>1.68</version>
		</dependency>
```

### 配置文件

```yaml
server:
  port: 8000

# 密码加密传输，前端公钥加密，后端私钥解密
sm2:
  private_key: |
    MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQgHXugwQeFgF6ELKVH
    l28evMOM/Cc2H/EGwv0wcIVYkUShRANCAASrr2gtqvS+u5A1Y7ywDh+6LnxYzum9
    sypgJ1bj0S+LyxQJCqg1jE7i/i4LADFJLvO1HY1LQW+6cIM2rLWXzQQG
```

### 测试加解密

```java
    /**
     * 测试SM2公钥加密与私钥解密
     *
     * @param formUser
     * @return
     */
    @PostMapping("/sm2")
    public String sm2(@RequestBody FormUser formUser) {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p\n" +
                "vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==";

        // 基本的加密与解密操作
        SM2 sm2 = new SM2(privateKey, publicKey);
        String encryptedString = sm2.encryptBcd(formUser.getUsername(), KeyType.PublicKey);
        String decryptedString = StrUtil.utf8Str(sm2.decryptFromBcd(encryptedString, KeyType.PrivateKey));

        log.info("密文：" + encryptedString);
        log.info("明文：" + decryptedString);

        // 不同格式的秘钥
        String publicKeyBase64 = sm2.getPublicKeyBase64();
        log.info("Base64公钥：" + publicKeyBase64);
        String privateKeyBase64 = sm2.getPrivateKeyBase64();
        log.info("Base64私钥：" + privateKeyBase64);

        String hexPublicKey = HexUtil.encodeHexStr(((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false));
        log.info("16进制的公钥：" + hexPublicKey);
        String hexPrivateKey = HexUtil.encodeHexStr(sm2.getPrivateKey().getEncoded());
        log.info("16进制的私钥：{}，格式：{}", hexPrivateKey, sm2.getPrivateKey().getFormat());

        // 使用Hutool生成秘钥对
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        PublicKey pub = pair.getPublic();
        byte[] pubKey = pub.getEncoded();
        log.info("Hutool生成公钥：{}，格式：{}", HexUtil.encodeHexStr(pubKey), pub.getFormat());

        PrivateKey pri = pair.getPrivate();
        byte[] priKey = pri.getEncoded();
        log.info("Hutool生成私钥：{}，格式：{}", HexUtil.encodeHexStr(priKey), pri.getFormat());

        //提取Q值转为16进制
        String qHex = HexUtil.encodeHexStr(((BCECPublicKey) pub).getQ().getEncoded(false));
        log.info("qHex：" + qHex);

        return decryptedString;
    }
```

![2022-10-15-SM2Test.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-10-15-SM2Test.jpg)

如果对 `SM2` 的秘钥格式有疑问，可参考这个 `GitHub` 示例：[https://github.com/luanxuechao/hutool-js-sm2-demo/blob/main/java-sm2/src/TestClass.java](https://github.com/luanxuechao/hutool-js-sm2-demo/blob/main/java-sm2/src/TestClass.java)，应该可以解答您的困惑。

Note：记得在 `SpringSecurity` 的配置文件中放行新增的测试接口。

```java
     http.authorizeRequests()
                // 记得放行匿名测试接口与认证接口
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/auth/sm2").permitAll()
                .anyRequest().authenticated()
```

### 认证接口

```java
  /**
     * 认证接口，其中使用SM2国密算法进行私钥解密
     *
     * @param formUser 加密后的用户信息
     * @param request
     * @return 认证后的用户
     */
    @PostMapping("/login")
    public String login(@RequestBody FormUser formUser, HttpServletRequest request) {
        log.info("formUser encrypted: {}", formUser);

        // 用户信息SM2私钥解密，使用hutool中的工具类进行解密
        SM2 sm2 = new SM2(privateKey, null);
        String username = StrUtil.utf8Str(sm2.decryptFromBcd(formUser.getUsername(), KeyType.PrivateKey));
        log.info("Username decrypted: {}", username);

        String password = StrUtil.utf8Str(sm2.decrypt(formUser.getPassword(), KeyType.PrivateKey));
        log.info("Password decrypted: {}", password);

        log.info("Userinfo decrypted: {}, {}", username, password);

        // 核验用户名密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication: {}", authentication);

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
```

![2022-10-15-FrontEncrypt.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-10-15-FrontEncrypt.jpg)

![2022-10-15-BackDecrypt.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-10-15-BackDecrypt.jpg)

## Vue前端服务如何集成SM2？

### 安装依赖

```
npm install sm-crypto
```

此时， `package.json` 的依赖变为：

```json
  "dependencies": {
    "axios": "^0.21.1",
    "core-js": "^3.6.5",
    "sm-crypto": "^0.3.11",
    "vue": "^3.0.0"
  },
```

具体用法可参考 `NPM` 文档：[https://www.npmjs.com/package/sm-crypto](https://www.npmjs.com/package/sm-crypto)

### 集成SM2：sm-crypto

在需要使用 `SM2` 的组件中引入 `import { sm2 } from "sm-crypto";` 。最终的前端登录组件代码如下：

Note：前端库 `sm-crypto` 对公钥的格式要求为**04开头的16进制公钥**，不可以使用 `OpenSSL` 直接生成的带换行的公钥形式。

```
<template>
  <div>
    <span>用户名</span><input type="text" v-model="user.username" />
    <span>密码</span><input type="password" v-model="user.password" />
    <input type="submit" v-on:click="login" value="登录" />
  </div>
</template>
<script>
import { defineComponent } from "vue";
import axios from "axios";
import { sm2 } from "sm-crypto";

export default defineComponent({
  name: "SM2Demo",
  setup() {},
  data() {
    return {
      user: { username: "dev", password: "123" },
//       publicKey: `MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p
// vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==`,
      publicKey: '04abaf682daaf4bebb903563bcb00e1fba2e7c58cee9bdb32a602756e3d12f8bcb14090aa8358c4ee2fe2e0b0031492ef3b51d8d4b416fba708336acb597cd0406'
    };
  },
  mounted() {
    this.login();
  },
  methods: {
    login: function () {
      // 密文前面需要加上04标志位，否则后端解密失败
      let userinfo = {
        username: "04" + sm2.doEncrypt(this.user.username, this.publicKey, 1),
        password: "04" + sm2.doEncrypt(this.user.password, this.publicKey, 1),
      };
      console.log(userinfo);

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
    }
  },
});
</script>
```

## 我遇到了哪些问题？

### 如何升级OpenSSL？

`openssl1.1.1` 以上的版本提供了对国密 `SM2` 算法的支持。 `https://www.openssl.org/news/cl111.txt` 。若版本低于 `1.1.1` ，则进行以下升级过程：

* 下载

```bash
# 先查看openssl版本
[root@hadoop1 ~]# openssl version
OpenSSL 1.0.2k-fips  26 Jan 2017
[root@hadoop1 ~]# cd /opt/
[root@hadoop1 opt]# wget https://www.openssl.org/source/openssl-1.1.1q.tar.gz
--2022-10-07 15:39:22--  https://www.openssl.org/source/openssl-1.1.1q.tar.gz
正在解析主机 www.openssl.org (www.openssl.org)... 104.69.242.206, 2600:140b:2:9a6::c1e, 2600:140b:2:9a4::c1e
正在连接 www.openssl.org (www.openssl.org)|104.69.242.206|:443... 已连接。
错误: 无法验证 www.openssl.org 的由 “/C=US/O=Let's Encrypt/CN=R3” 颁发的证书:
  颁发的证书已经过期。
要以不安全的方式连接至 www.openssl.org，使用“--no-check-certificate”。

[root@hadoop1 opt]# wget https://www.openssl.org/source/openssl-1.1.1q.tar.gz --no-check-certificate
--2022-10-07 15:40:31--  https://www.openssl.org/source/openssl-1.1.1q.tar.gz
正在解析主机 www.openssl.org (www.openssl.org)... 104.69.242.206, 2600:140b:2:9a4::c1e, 2600:140b:2:9a6::c1e
正在连接 www.openssl.org (www.openssl.org)|104.69.242.206|:443... 已连接。
警告: 无法验证 www.openssl.org 的由 “/C=US/O=Let's Encrypt/CN=R3” 颁发的证书:
  颁发的证书已经过期。
已发出 HTTP 请求，正在等待回应... 200 OK
长度：9864061 (9.4M) [application/x-gzip]
正在保存至: “openssl-1.1.1q.tar.gz”

100%[=================================================================>] 9,864,061   3.58MB/s 用时 2.6s
```

* 升级步骤

```
# 下载
wget https://www.openssl.org/source/openssl-1.1.1q.tar.gz
# 解压
tar -xvf openssl-1.1.1q.tar.gz
# 进入
cd openssl-1.1.1q
# 配置
./config
# 编译
make
# 安装
make install

#备份旧的命令 
mv /usr/bin/openssl /usr/bin/openssl.old
mv /usr/include/openssl /usr/include/openssl.old

# 添加软连接
ln -s /usr/local/ssl/bin/openssl /usr/bin/openssl
ln -s /usr/local/ssl/include/openssl /usr/include/openssl

# 写入openssl库文件的搜索路径
echo "/usr/local/lib64" >> /etc/ld.so.conf

# 使修改后的搜索路径生效 
ldconfig -v

# 再次查看openssl版本，确认是否更新
openssl version
```

* 确认新版本

```bash
[root@hadoop1 ~]# openssl version
OpenSSL 1.1.1q  5 Jul 2022
```

### YAML中如何换行？

前面知道，我们是通过 `OpenSSL` 生成的私钥，这种格式是带换行符的，如果我配置到了 `YAML` 描述文件中，则需要对换行进行特殊处理， `YAML` 描述文件中的多行字符串可以使用 `|` 保留换行符，使用 `>` 将换行符替换为空格。

```yaml
# 使用竖线 | 保留换行符
sm2:
  private_key: |
    MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQgHXugwQeFgF6ELKVH
    l28evMOM/Cc2H/EGwv0wcIVYkUShRANCAASrr2gtqvS+u5A1Y7ywDh+6LnxYzum9
    sypgJ1bj0S+LyxQJCqg1jE7i/i4LADFJLvO1HY1LQW+6cIM2rLWXzQQG
```

### 后端报错

> `java.security.InvalidKeyException: IOException : Unknown named curve: 1.2.156.10197.1.301`

国密证书使用了自有的椭圆曲线，所以无法使用JDK自带的java.security解析证书，需要引入BouncyCastle的bcprov-jdk15on依赖。http://javadox.com/maven/dependecy/org.bouncycastle/bcprov-jdk15on/1.53.html

```xml
		<dependency>
			<groupId>org.bouncycastle</groupId>
			<artifactId>bcprov-jdk15on</artifactId>
			<version>1.53</version>
		</dependency>
```

### java.security.spec. InvalidKeySpecException: encoded key spec not recognised

升级版本 `1.53` ——> `1.68` 。

```xml
		<dependency>
			<groupId>org.bouncycastle</groupId>
			<artifactId>bcprov-jdk15on</artifactId>
			<version>1.68</version>
		</dependency>		
```

### 前端加密失败

> 报错：Uncaught TypeError: publicKey is null

![2022-10-15-Exception.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-10-15-Exception.jpg)

公钥格式不正确，前端库 `sm-crypto` 对公钥的格式要求为**04开头的16进制公钥**，可在后端通过 `String hexPublicKey = HexUtil.encodeHexStr(((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false));` 将

```
MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p
vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==
```

转换为**16进制格式公钥**：

```
04abaf682daaf4bebb903563bcb00e1fba2e7c58cee9bdb32a602756e3d12f8bcb14090aa8358c4ee2fe2e0b0031492ef3b51d8d4b416fba708336acb597cd0406
```

### 后端解密失败

> 后端解密失败，报错： `java.lang.IllegalArgumentException: Invalid point encoding 0x-5`

> 后端解密失败，报错： `cn.hutool.crypto. CryptoException: invalid cipher text`

这两个问题比较诡异，可能是后端解密问题：后端解密时，要在密文前面加上04（或者前端加密后在密文前直接加上04）。
**但主要是因为我的前端加密有问题，因为我对数字类型加密，前端使用公钥加密成功，导致后端无法解密。这应该是 `sm-crypto` 的一个Bug。**

---

## Source Code 

* [Source Code: Github](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-security/springboot-security-sm2/springboot-security-sm2-backend)

## Reference

* [在线生成SM2秘钥对](https://const.net.cn/tool/sm2/genkey/)

* [在线加解密](https://the-x.cn/cryptography/Sm2.aspx)

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***