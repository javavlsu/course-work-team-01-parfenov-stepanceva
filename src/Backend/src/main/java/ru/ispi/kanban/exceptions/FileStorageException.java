package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends ApiException {
    public FileStorageException(String message) {
        super(ErrorCode.FILE_STORAGE_ERROR, message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(ErrorCode.FILE_STORAGE_ERROR, message, cause);
    }
}
