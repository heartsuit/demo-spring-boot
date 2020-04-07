1. SpringBoot+SpringSecurity 跑起来
继承 WebSecurityConfigurerAdapter，重写两个configure方法，分别做认证配置、拦截配置
采用简单的in-memory 模式演示认证流程与权限配置
采用默认的login、logout页面，以及Whitelabel错误页面
解决报错： There is no PasswordEncoder mapped for the id "null"
废弃了NoOpPasswordEncoder
@Bean
public PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance(); // Deprecated
}
PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
默认采用bcrypt

2. SpringBoot+SpringSecurity 自定义登录页面配置，自定义Error页面配置
自定义登录/退出页面
引入模板引擎
错误页面配置
disable csrf
~~封装接口响应~~

3. SpringBoot+SpringSecurity MySQL数据库
