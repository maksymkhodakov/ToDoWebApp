package com.example.todowebapp.security;

import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.domain.enumerated.Privilege;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (permission instanceof Privilege privilege &&
                authentication != null &&
                authentication.getPrincipal() instanceof User userPrincipal) {
            return hasPrivilege(privilege, userPrincipal);
        }
        return false;
    }

    private boolean hasPrivilege(Privilege permission, User principal) {
        return principal.getAuthorities()
                .stream()
                .anyMatch(p -> p.getAuthority().equals(permission.name()));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetId, permission);
    }
}
