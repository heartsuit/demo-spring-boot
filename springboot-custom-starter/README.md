## 如何自定义一个starter？ 

1. import-seclector，自定义ImportSelector

2. conditional，自定义条件配置类

3. starter，自定义Starter

- 官方命名：spring-boot-starter-模块名称
- 自定义命名：模块名称-spring-boot-starter

自定义starter步骤如下：

- 导入依赖

- 自定义属性RedissonProperties

- 自定义配置RedissonAutoConfiguration

- 编写spring.factories

- 本地/私服安装：mvn clean install

- 引入并使用自定义的starter