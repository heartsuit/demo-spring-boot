package com.heartsuit.config;

import com.heartsuit.filter.JwtAccessDeniedHandler;
import com.heartsuit.filter.JwtAuthenticationEntryPoint;
import com.heartsuit.filter.JwtAuthenticationFilter;
import com.heartsuit.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Heartsuit
 * @Date 2021-01-10
 */
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;

    public SecurityConfig(JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint authenticationErrorHandler) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.authenticationErrorHandler = authenticationErrorHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = passwordEncoder();

        String yourPassword = "123";
        log.info("Encoded password: " + encoder.encode(yourPassword));

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
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless
                .and()
                .formLogin()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
                        log.info("Login Successfully");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String token = JwtUtil.createToken(authentication);
                        httpServletResponse.getWriter().write(token);
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
                        log.info("Login Error");
                        httpServletResponse.getWriter().write(e.getLocalizedMessage());
                    }
                })
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler);
    }
}

