package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CommentAccessDeniedException extends ApiException {
    public CommentAccessDeniedException(String message) {
        super(ErrorCode.COMMENT_ACCESS_DENIED, message);
    }
}
