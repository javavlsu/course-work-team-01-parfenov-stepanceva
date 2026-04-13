package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class InvitationNotFoundException extends ApiException {
    public InvitationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
