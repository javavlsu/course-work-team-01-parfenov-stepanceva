package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;

public class AttachmentNotFoundException extends ApiException {
    public AttachmentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
