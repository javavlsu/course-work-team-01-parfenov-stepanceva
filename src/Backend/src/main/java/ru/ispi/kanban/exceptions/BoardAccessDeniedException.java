package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class BoardAccessDeniedException extends ApiException {
    public BoardAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
