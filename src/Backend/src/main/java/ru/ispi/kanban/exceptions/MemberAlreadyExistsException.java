package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MemberAlreadyExistsException extends ApiException {
    public MemberAlreadyExistsException(String message) {
        super(ErrorCode.GROUP_MEMBER_EXISTS, message);
    }
}
