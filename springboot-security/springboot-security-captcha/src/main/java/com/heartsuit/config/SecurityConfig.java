package com.heartsuit.config;

/**
 * @Author Heartsuit
 * @Date 2020-12-26
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasAuthority("p1")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .formLogin()
                .loginPage("/login");
    }
}
