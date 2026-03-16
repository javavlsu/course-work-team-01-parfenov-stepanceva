package ru.ispi.kanban.payload;

public record UpdateBoardPayload(
        String title,
        String description
) {
}
