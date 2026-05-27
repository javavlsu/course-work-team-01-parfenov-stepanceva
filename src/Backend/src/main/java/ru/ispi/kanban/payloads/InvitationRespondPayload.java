package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotNull;

public record InvitationRespondPayload(

        @NotNull(message = "accept must not be null")
        Boolean accept
) {
}
