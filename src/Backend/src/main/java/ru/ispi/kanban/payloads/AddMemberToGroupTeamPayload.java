package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotNull;
import ru.ispi.kanban.enums.GroupRole;

public record AddMemberToGroupTeamPayload(

        @NotNull(message = "userId must not be empty")
        Integer userId,

        @NotNull(message = "Role must not be null")
        GroupRole role)
{
}
