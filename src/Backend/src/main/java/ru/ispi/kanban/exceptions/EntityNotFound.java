package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class EntityNotFound extends ApiException {
    public EntityNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
