package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchUserByEmailException extends ApiException {
    public NoSuchUserByEmailException(String message) {
        super(ErrorCode.USER_NOT_FOUND_BY_EMAIL, message);
    }
}
