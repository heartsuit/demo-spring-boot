package com.heartsuit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @Author Heartsuit
 * @Date 2021-01-10
 */
public class JwtUtil {
    /**
     * 生成 jwt token
     *
     * @param authentication
     * @return
     */
    public static String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + JwtConstant.VALIDITY_SECONDS * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(JwtConstant.AUTH_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, JwtConstant.SECRET)
                .setExpiration(validity)
                .compact();
    }

    /**
     * 解密 jwt token
     *
     * @param token
     * @return
     */
    public static Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JwtConstant.SECRET)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(JwtConstant.AUTH_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 从请求头信息中解析出token
     *
     * @param request
     * @return
     */
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
