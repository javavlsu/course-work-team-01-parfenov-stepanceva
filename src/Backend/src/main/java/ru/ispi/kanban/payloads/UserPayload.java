package ru.ispi.kanban.payloads;

public record UserPayload(
        String email,
        String name,
        String password,
        String avatarUrl
) {
}
