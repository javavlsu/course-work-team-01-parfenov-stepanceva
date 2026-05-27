package ru.ispi.kanban.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String errorCode;

    public ApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
