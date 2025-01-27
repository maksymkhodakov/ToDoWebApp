package com.example.todowebapp.security;

import com.example.todowebapp.domain.enumerated.FeatureAction;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(final Authentication authentication,
                                 final Object targetDomainObject,
                                 final Object permission) {
        if (permission instanceof FeatureAction featureAction && authentication instanceof UserAuthentication userAuthentication) {
            return hasAction(featureAction, userAuthentication);
        }
        return false;
    }

    @Override
    public boolean hasPermission(final Authentication authentication,
                                 final Serializable targetId,
                                 final String targetType,
                                 final Object permission) {
        return hasPermission(authentication, targetId, permission);
    }

    private boolean hasAction(final FeatureAction featureAction,
                              final UserAuthentication userAuthentication) {
        final Set<String> features = userAuthentication.getPrincipal().getFeatures();
        if (features == null) {
            return false;
        }
        return features.contains(featureAction.getName());
    }
}
