package com.hariyali.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hariyali.entity.Roles;
import com.hariyali.entity.Users;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private Users user;


	public CustomUserDetails(Users user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Roles roles = this.user.getUserRole();

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		System.err.println(roles.getUsertypeName());
		authorities.add(new SimpleGrantedAuthority(roles.getUsertypeName()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		
		if(user.getEmailId()!=null)
		{
			return this.user.getEmailId();
		}
		return this.user.getDonorId();
		
		
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

}
