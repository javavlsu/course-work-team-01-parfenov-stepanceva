package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEmailInvitationPayload(

        @NotBlank(message = "email must not be empty")
        @Email(message = "email should be valid")
        String email,

        @NotNull(message = "expiresInDays must not be null")
        @Min(value = 1, message = "expiresInDays must be at least 1")
        @Max(value = 30, message = "expiresInDays must not exceed 30")
        Integer expiresInDays
) {
}
