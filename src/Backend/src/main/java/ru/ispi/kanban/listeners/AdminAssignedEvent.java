package ru.ispi.kanban.listeners;

import ru.ispi.kanban.entities.User;

public record AdminAssignedEvent(
        Integer groupId,
        User user
) {
}
