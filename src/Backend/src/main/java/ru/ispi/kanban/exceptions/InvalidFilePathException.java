package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFilePathException extends ApiException {
    public InvalidFilePathException(String message) {
        super(ErrorCode.FILE_INVALID_PATH, message);
    }
}
