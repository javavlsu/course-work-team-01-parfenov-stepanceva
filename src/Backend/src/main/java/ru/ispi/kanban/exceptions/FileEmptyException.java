package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileEmptyException extends ApiException {
    public FileEmptyException(String message) {
        super(ErrorCode.FILE_EMPTY, message);
    }
}
