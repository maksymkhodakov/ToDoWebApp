package com.example.todowebapp.util;

import com.example.todowebapp.domain.entity.User;
import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserSecurityUtil {

    private UserSecurityUtil() {
    }

    public static User getCurrentUser() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null && auth.getPrincipal() instanceof User user) {
            return user;
        } else {
            throw new ApiException(ErrorCode.USER_NOT_FOUND_IN_SECURITY_CONTEXT);
        }
    }
}
