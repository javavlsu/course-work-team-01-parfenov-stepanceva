package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpiredException extends ApiException {
    public RefreshTokenExpiredException(String message) {
        super(ErrorCode.AUTH_REFRESH_EXPIRED, message);
    }
}
