package ru.ispi.kanban.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvitationNotFoundException extends ApiException {
    public InvitationNotFoundException(String message) {
        super(ErrorCode.INVITATION_NOT_FOUND, message);
    }
}
