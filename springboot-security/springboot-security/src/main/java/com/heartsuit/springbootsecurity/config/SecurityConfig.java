package com.heartsuit.springbootsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // @Bean
    // public UserDetailsService userDetailsService(){
    //     InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    //     manager.createUser(User.withUsername("dev").password("123").authorities("p1").build());
    //     manager.createUser(User.withUsername("test").password("123").authorities("p2").build());
    //     return manager;
    // }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        String yourPassword = "123";
        System.out.println("Encoded password: " + encoder.encode(yourPassword));

		// Config account info and permissions
		auth.inMemoryAuthentication().withUser("dev").password(encoder.encode(yourPassword)).authorities("p1");
        auth.inMemoryAuthentication().withUser("test").password(encoder.encode(yourPassword)).authorities("p2");
        /*.authorities(...) can not be omited, java.lang.IllegalArgumentException: Cannot pass a null GrantedAuthority collection*/
	}

    // @Bean
    // public PasswordEncoder passwordEncoder(){
    //     return NoOpPasswordEncoder.getInstance(); // Deprecated
    // }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasAuthority("p1")
                .antMatchers("/user/query").hasAuthority("p2")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .formLogin() // Support form and HTTPBasic 
                .successForwardUrl("/greeting");// custom login success page, a POST request

    }
}
