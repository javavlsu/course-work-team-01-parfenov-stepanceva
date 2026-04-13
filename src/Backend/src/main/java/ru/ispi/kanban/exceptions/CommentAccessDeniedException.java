package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class CommentAccessDeniedException extends ApiException {
    public CommentAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
