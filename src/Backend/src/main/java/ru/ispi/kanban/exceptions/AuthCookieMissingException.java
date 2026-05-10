package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthCookieMissingException extends ApiException {
    public AuthCookieMissingException(String message) {
        super(ErrorCode.AUTH_COOKIE_MISSING, message);
    }
}
