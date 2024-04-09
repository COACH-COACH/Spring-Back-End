package com.example.demo.model.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.model.entity.User;

import io.jsonwebtoken.lang.Collections;

public class CustomUserDetails implements UserDetails {

    private final User userEntity;

    public CustomUserDetails(User userEntity) {
        this.userEntity = userEntity;
    }

    // role값 반환(우리는 X)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return userEntity.getLoginPw();
    }

    @Override
    public String getUsername() {
        return userEntity.getLoginId();
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