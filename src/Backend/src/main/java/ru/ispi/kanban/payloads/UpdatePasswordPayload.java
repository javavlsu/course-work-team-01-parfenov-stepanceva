package ru.ispi.kanban.payloads;

public record UpdatePasswordPayload(
        String oldPassword,
        String newPassword
) {
}