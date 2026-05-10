package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BoardAccessDeniedException extends ApiException {
    public BoardAccessDeniedException(String message) {
        super(ErrorCode.BOARD_ACCESS_DENIED, message);
    }
}
