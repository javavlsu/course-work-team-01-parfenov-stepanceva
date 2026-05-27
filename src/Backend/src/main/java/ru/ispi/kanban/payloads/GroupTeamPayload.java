package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupTeamPayload(

        @NotBlank(message = "name must not be empty")
        @Size(min = 3, max = 100, message = "name of team must be at least 3 characters long, max 100")
        String name,

        String description){
}
