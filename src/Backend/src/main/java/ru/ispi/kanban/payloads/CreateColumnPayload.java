package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateColumnPayload(

        @NotBlank(message = "title must not be empty")
        @Size(min = 1, max = 100, message = "title must be between 1 and 100 characters")
        String title
) {
}
