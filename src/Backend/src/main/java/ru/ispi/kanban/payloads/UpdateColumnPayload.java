package ru.ispi.kanban.payloads;

public record UpdateColumnPayload(
        String title,
        Long position
) {
}
