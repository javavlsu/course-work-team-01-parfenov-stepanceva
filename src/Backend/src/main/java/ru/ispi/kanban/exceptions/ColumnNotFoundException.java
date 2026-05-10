package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ColumnNotFoundException extends ApiException {
    public ColumnNotFoundException(String message) {
        super(ErrorCode.COLUMN_NOT_FOUND, message);
    }
}
