package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
public class FileTooLargeException extends ApiException {
    public FileTooLargeException(String message) {
        super(ErrorCode.FILE_TOO_LARGE, message);
    }
}
