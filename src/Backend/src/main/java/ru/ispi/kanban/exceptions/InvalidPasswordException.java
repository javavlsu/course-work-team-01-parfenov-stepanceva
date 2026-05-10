package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends ApiException {
    public InvalidPasswordException(String message) {
        super(ErrorCode.USER_INVALID_PASSWORD, message);
    }
}
