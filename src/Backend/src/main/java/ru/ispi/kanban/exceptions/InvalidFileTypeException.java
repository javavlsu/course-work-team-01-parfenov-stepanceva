package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class InvalidFileTypeException extends ApiException {
    public InvalidFileTypeException(String message) {
        super(ErrorCode.FILE_INVALID_TYPE, message);
    }
}
