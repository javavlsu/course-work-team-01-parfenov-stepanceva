package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends ApiException {
    public CommentNotFoundException(String message) {
        super(ErrorCode.COMMENT_NOT_FOUND, message);
    }
}
