package ru.ispi.canban.payload;

public record UserPayload(
        String email,
        String name,
        String password,
        String avatarUrl
) {
}
