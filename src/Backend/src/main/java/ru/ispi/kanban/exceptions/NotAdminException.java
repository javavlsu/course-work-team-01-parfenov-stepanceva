package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class NotAdminException extends ApiException {
    public NotAdminException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
