package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends ApiException {
    public TokenExpiredException(String message) {
        super(ErrorCode.AUTH_TOKEN_EXPIRED, message);
    }
}
