package ru.ispi.kanban.payload;

import jakarta.validation.constraints.NotNull;
import ru.ispi.kanban.enums.GroupRole;

public record UpdateMemberRoleInGroupTeamPayload(

        @NotNull(message = "Role must not be null")
        GroupRole role
) {
}
