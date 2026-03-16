package ru.ispi.kanban.payload;

public record CreateBoardPayload(
        String title,
        String description
) {
}
