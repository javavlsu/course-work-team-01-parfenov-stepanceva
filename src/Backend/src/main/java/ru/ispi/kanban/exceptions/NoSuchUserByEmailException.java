package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class NoSuchUserByEmailException extends ApiException {
    public NoSuchUserByEmailException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
