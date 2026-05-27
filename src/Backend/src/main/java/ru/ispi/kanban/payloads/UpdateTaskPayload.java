package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTaskPayload(

        Integer columnId,

        @Size(min = 1, max = 200, message = "title must be between 1 and 200 characters")
        String title,

        @Size(max = 5000, message = "description must not exceed 5000 characters")
        String description,

        Integer assigneeId,

        @Positive(message = "position must be a positive number")
        Long position,

        @Future(message = "deadline must be in the future")
        LocalDateTime deadline,

        @Size(max = 50, message = "priority must not exceed 50 characters")
        String priority,

        @Size(max = 50, message = "status must not exceed 50 characters")
        String status
) {
}
