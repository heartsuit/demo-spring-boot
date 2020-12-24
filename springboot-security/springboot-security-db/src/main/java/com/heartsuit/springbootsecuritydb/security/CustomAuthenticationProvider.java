package com.heartsuit.springbootsecuritydb.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Author Heartsuit
 * @Date 2020-12-02
 */
@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Start custom authentication");
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        log.info("userDetails: {}", userDetails);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = authentication.getCredentials().toString();
        log.info("rawPassword: {}", rawPassword);
        String encodePassword = passwordEncoder.encode(rawPassword);
        log.info("encodePassword: {}", encodePassword);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodePassword, userDetails.getAuthorities());
        log.info("authenticationToken: {}", authenticationToken);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
