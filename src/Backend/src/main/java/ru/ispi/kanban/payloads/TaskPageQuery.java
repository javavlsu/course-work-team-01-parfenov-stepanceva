package ru.ispi.kanban.payloads;

import ru.ispi.kanban.enums.TaskPriority;
import ru.ispi.kanban.enums.TaskStatus;

public record TaskPageQuery(
        String search,
        TaskPriority priority,
        TaskStatus status,
        Integer columnId,
        Integer assigneeId,
        String sortBy,
        String sortDir,
        Integer page,
        Integer size
) {
    public int pageOrDefault() {
        return page == null || page < 0 ? 0 : page;
    }

    public int sizeOrDefault() {
        if (size == null || size <= 0) return 10;
        return Math.min(size, 100);
    }
}
