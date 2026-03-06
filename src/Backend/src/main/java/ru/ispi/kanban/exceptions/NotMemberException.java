package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class NotMemberException extends ApiException{
    public NotMemberException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
