package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateColumnPayload(

        @Size(min = 1, max = 100, message = "title must be between 1 and 100 characters")
        String title,

        @Positive(message = "position must be a positive number")
        Long position
) {
}
