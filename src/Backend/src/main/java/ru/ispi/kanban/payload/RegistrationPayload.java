package ru.ispi.kanban.payload;

import jakarta.validation.constraints.NotNull;

public record RegistrationPayload(
        @NotNull
        String email,
        @NotNull
        String name,
        @NotNull
        String password
) {
}
