package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchUserByIdException extends ApiException {
    public NoSuchUserByIdException(String message) {
        super(ErrorCode.USER_NOT_FOUND_BY_ID, message);
    }
}
