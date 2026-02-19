package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class AuthException extends ApiException{


    public AuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
