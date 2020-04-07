package com.heartsuit.springbootsecuritydb.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class UserDto implements UserDetails {

	/**
   *
   */
  private static final long serialVersionUID = 1L;
  
  private Integer id;
	private String username;
	private String password;
	private String realname;
	private String mobile;
  private boolean enabled;
  private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;

	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
