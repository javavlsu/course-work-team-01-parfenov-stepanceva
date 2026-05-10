package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotMemberException extends ApiException {
    public NotMemberException(String message) {
        super(ErrorCode.GROUP_NOT_MEMBER, message);
    }
}
