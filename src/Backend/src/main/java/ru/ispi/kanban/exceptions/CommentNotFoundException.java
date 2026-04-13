package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApiException {
    public CommentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
