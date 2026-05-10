package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class InvitationExpiredException extends ApiException {
    public InvitationExpiredException(String message) {
        super(ErrorCode.INVITATION_EXPIRED, message);
    }
}
