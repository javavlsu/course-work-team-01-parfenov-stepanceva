package ru.ispi.kanban.payloads;

import java.time.LocalDateTime;

public record CreateTaskPayload(
        Integer columnId,

        String title,

        String description,

        Integer assigneeId,

        Long position,

        LocalDateTime deadline,

        String priority,

        String status
) {
}
