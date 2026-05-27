package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentPayload(

        @NotBlank(message = "text must not be empty")
        @Size(min = 1, max = 2000, message = "comment text must be between 1 and 2000 characters")
        String text
) {
}
