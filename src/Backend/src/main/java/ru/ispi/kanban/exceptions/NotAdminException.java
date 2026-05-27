package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAdminException extends ApiException {
    public NotAdminException(String message) {
        super(ErrorCode.GROUP_NOT_ADMIN, message);
    }
}
