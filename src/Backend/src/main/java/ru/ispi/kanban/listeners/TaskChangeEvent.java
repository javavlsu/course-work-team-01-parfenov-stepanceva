package ru.ispi.kanban.listeners;

import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.enums.ActionType;

public record TaskChangeEvent(
        Task task,

        Integer userId,

        ActionType actionType,

        String changedAttribute,

        String oldValue,

        String newValue
) {
}