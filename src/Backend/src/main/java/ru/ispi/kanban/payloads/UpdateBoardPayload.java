package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBoardPayload(

        @NotBlank(message = "title must not be empty")
        @Size(min = 3, max = 100, message = "title must be at least 3 characters long, max 100")
        String title,

        String description
) {
}
