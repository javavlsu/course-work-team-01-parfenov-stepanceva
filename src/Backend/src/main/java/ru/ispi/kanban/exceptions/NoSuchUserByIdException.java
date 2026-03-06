package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class NoSuchUserByIdException extends ApiException {
    public NoSuchUserByIdException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
