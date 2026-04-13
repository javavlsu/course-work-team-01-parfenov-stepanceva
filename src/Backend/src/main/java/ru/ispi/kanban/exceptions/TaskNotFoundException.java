package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends ApiException {
    public TaskNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
