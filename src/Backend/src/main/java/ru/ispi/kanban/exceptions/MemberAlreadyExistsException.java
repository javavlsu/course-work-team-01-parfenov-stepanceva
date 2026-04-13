package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class MemberAlreadyExistsException extends ApiException {
    public MemberAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
