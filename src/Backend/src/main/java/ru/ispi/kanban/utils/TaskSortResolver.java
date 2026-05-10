package ru.ispi.kanban.utils;

import org.springframework.data.domain.Sort;

import java.util.Set;

public final class TaskSortResolver {

    private static final String DEFAULT_FIELD = "createdAt";

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "title", "priority", "status", "deadline", "createdAt", "position"
    );

    private TaskSortResolver() {
    }

    public static Sort resolve(String sortBy, String sortDir) {
        String field = ALLOWED_FIELDS.contains(sortBy) ? sortBy : DEFAULT_FIELD;
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort primary = Sort.by(direction, field);
        // вторичный ключ для стабильной пагинации
        return "id".equals(field) ? primary : primary.and(Sort.by(Sort.Direction.ASC, "id"));
    }
}
