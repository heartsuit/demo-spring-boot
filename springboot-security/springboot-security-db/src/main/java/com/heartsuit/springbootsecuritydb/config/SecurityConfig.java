package com.heartsuit.springbootsecuritydb.config;

import java.util.List;

import com.heartsuit.springbootsecuritydb.dto.PermissionDto;
import com.heartsuit.springbootsecuritydb.handler.CustomAuthenctiationFailureHandler;
import com.heartsuit.springbootsecuritydb.mapper.PermissionMapper;
import com.heartsuit.springbootsecuritydb.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    CustomAuthenctiationFailureHandler failureHandler;

    @Autowired
    PermissionMapper permissionMapper;

    // Method2:
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Method1:
        // There is no PasswordEncoder mapped for the id "null"
        // PasswordEncoder encoder = new BCryptPasswordEncoder();        
        // String yourPassword = "123";
        // System.out.println("Encoded password: " + encoder.encode(yourPassword));
        // auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder);


        auth.userDetailsService(customUserDetailsService);
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http
        .authorizeRequests();
        
        List<PermissionDto> permissions = permissionMapper.getAllPermissions();

        for (PermissionDto permission : permissions) {
            authorizeRequests.antMatchers(permission.getUrl()).hasAuthority(permission.getCode());
        }
        authorizeRequests
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .formLogin() // Support form and HTTPBasic
                .loginPage("/login")
                .successForwardUrl("/greeting")// custom login success page, a POST request
                .failureHandler(failureHandler)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout");
    }
}
