package com.heartsuit.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @Author Heartsuit
 * @Date 2020-12-19
 */
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        String yourPassword = "123";
//        log.info("Password: {}", passwordEncoder().encode(yourPassword));
//        manager.createUser(User.withUsername("dev").password(passwordEncoder().encode(yourPassword)).roles("dev").build());
//        manager.createUser(User.withUsername("test").password(passwordEncoder().encode(yourPassword)).authorities("ROLE_test").build());
//        return manager;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = passwordEncoder();

        String yourPassword = "123";
        System.out.println("Encoded password: " + encoder.encode(yourPassword));

        // Config account info and permissions
        auth.inMemoryAuthentication()
                .withUser("dev").password(encoder.encode(yourPassword)).roles("dev", "test")
                .and()
                .withUser("test").password(encoder.encode(yourPassword)).authorities("ROLE_test");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasRole("dev")
                .antMatchers("/user/query").hasAuthority("ROLE_test")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .formLogin();
//                .and()
//                .rememberMe()
//                .and()
//                .sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);
    }

//    @Bean
//    HttpSessionEventPublisher httpSessionEventPublisher (){
//        return new HttpSessionEventPublisher();
//    }

}

