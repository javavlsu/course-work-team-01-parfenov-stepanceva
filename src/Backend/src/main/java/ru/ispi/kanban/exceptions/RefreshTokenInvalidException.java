package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenInvalidException extends ApiException {
    public RefreshTokenInvalidException(String message) {
        super(ErrorCode.AUTH_REFRESH_INVALID, message);
    }
}
