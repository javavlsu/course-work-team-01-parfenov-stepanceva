package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ApiException {
    public InvalidPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
