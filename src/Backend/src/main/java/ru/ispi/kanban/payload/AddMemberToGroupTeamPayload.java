package ru.ispi.kanban.payload;

public record AddMemberToGroupTeamPayload(Integer userId,
                                          String role) {
}
