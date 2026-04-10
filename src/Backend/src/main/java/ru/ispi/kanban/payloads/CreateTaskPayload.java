package ru.ispi.kanban.payloads;

import java.time.LocalDateTime;

public record CreateTaskPayload(
        Integer columnId,

        String title,

        String description,

        Integer assigneeId,

        //Long position, - решил что юзлесс, пусть созданный таск будет всегда первым в колонке

        LocalDateTime deadline,

        String priority,

        String status
) {
}
