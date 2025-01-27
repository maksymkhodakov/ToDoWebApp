package com.example.todowebapp.security;

import com.example.todowebapp.domain.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class UserAuthentication implements Authentication {

    @Serial
    private static final long serialVersionUID = -9156088741843433314L;

    private final UserDTO principal;
    private boolean authenticated = true;

    @Getter
    @Setter
    private String jwt;

    public UserAuthentication(UserDTO principal) {
        this.principal = principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.principal != null && this.principal.getRoles() != null) {
            return this.principal.getRoles()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return new ArrayList<>();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public UserDTO getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal == null ? null : principal.getUsername();
    }
}
