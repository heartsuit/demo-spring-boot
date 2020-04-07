1. SpringBoot+SpringSecurity 跑起来
默认用户名user，密码在控制台输出
可以在application.yml中配置用户认证信息
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

解决报错：Cannot pass a null GrantedAuthority collection
每个用户必须要有权限列表配置 authorities()