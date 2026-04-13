package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class ColumnNotFoundException extends ApiException {
    public ColumnNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
