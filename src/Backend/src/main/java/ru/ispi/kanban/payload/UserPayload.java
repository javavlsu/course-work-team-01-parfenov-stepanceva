package ru.ispi.kanban.payload;

public record UserPayload(
        String email,
        String name,
        String password,
        String avatarUrl
) {
}
