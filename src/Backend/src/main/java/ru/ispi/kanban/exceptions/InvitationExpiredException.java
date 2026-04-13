package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class InvitationExpiredException extends ApiException {
    public InvitationExpiredException(String message) {
        super(message, HttpStatus.GONE);
    }
}
