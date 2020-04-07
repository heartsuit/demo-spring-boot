package com.heartsuit.springbootsecurityform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // There is no PasswordEncoder mapped for the id "null"
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        String yourPassword = "123";
        System.out.println("Encoded password: " + encoder.encode(yourPassword));

		// Config account info and permissions
		auth.inMemoryAuthentication().withUser("dev").password(encoder.encode(yourPassword)).authorities("p1");
		auth.inMemoryAuthentication().withUser("test").password(encoder.encode(yourPassword)).authorities("p2");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/add").hasAuthority("p1")
                .antMatchers("/user/query").hasAuthority("p2")
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll() // Let other request pass
                .and()
                .csrf().disable() // turn off csrf, or will be 403 forbidden
                .formLogin() // Support form and HTTPBasic
                .loginPage("/login")
                .successForwardUrl("/greeting")// custom login success page, a POST request
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout");
    }
}
