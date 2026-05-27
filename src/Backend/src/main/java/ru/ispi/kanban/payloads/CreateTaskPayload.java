package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskPayload(

        @NotNull(message = "columnId must not be null")
        Integer columnId,

        @NotBlank(message = "title must not be empty")
        @Size(min = 1, max = 200, message = "title must be between 1 and 200 characters")
        String title,

        @Size(max = 5000, message = "description must not exceed 5000 characters")
        String description,

        Integer assigneeId,

        //Long position, - решил что юзлесс, пусть созданный таск будет всегда первым в колонке

        @Future(message = "deadline must be in the future")
        LocalDateTime deadline,

        @Size(max = 50, message = "priority must not exceed 50 characters")
        String priority,

        @Size(max = 50, message = "status must not exceed 50 characters")
        String status
) {
}
