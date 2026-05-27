package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateNamePayload(

        @NotBlank(message = "name must not be empty")
        @Pattern(
                regexp = "^[a-zA-Z0-9_а-яА-ЯёЁ]{3,20}$",
                message = "Nickname can only contain letters (Eng/Rus), digits and underscore (3-20 chars)"
        )
        String name
) {
}
