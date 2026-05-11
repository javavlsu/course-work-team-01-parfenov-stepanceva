package ru.ispi.kanban.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ispi.kanban.exceptions.AuthCookieMissingException;
import ru.ispi.kanban.security.CustomUserDetails;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails details)) {
            return null;
        }
        return details.getId();
    }

    public static Integer requireCurrentUserId() {
        Integer id = getCurrentUserId();
        if (id == null) {
            throw new AuthCookieMissingException("No authenticated user in security context");
        }
        return id;
    }
}
