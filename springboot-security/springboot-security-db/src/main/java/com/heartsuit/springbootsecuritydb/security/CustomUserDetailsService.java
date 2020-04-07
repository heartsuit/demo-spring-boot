package com.heartsuit.springbootsecuritydb.security;

import java.util.ArrayList;
import java.util.List;

import com.heartsuit.springbootsecuritydb.dto.PermissionDto;
import com.heartsuit.springbootsecuritydb.dto.UserDto;
import com.heartsuit.springbootsecuritydb.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDto user = userMapper.getUserByUsername(username);
    if (user != null) {
      List<PermissionDto> permissions = userMapper.getPermissionsByUsername(username);
      if (permissions != null) {
        System.out.println(user.getUsername() + " has these permissions: " + permissions);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        permissions.stream().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode())));
        // user.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("p1"))); // hard-coded permission
        user.setAuthorities(authorities);
      }
    }
    return user;
  }

}