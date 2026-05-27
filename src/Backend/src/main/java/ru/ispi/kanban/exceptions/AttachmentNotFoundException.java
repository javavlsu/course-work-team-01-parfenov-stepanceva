package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttachmentNotFoundException extends ApiException {
    public AttachmentNotFoundException(String message) {
        super(ErrorCode.ATTACHMENT_NOT_FOUND, message);
    }
}
