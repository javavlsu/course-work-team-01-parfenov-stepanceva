package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFound extends ApiException {
    public EntityNotFound(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }
}
