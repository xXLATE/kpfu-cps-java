package com.example.demo.config;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class GreenhousePermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasPermission(authentication, permission);
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission) {
        return hasPermission(authentication, permission);
    }

    private boolean hasPermission(Authentication authentication, Object permission) {
        if (authentication == null || permission == null || !authentication.isAuthenticated()) {
            return false;
        }

        String requiredPermission = permission.toString();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermission)
                        || authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
