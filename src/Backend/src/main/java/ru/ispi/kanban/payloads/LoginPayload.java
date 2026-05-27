package ru.ispi.kanban.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginPayload(

        @NotBlank(message = "email must not be empty")
        @Email(message = "email should be valid")
        String email,

        @NotBlank(message = "password must not be empty ")
        @Size(min = 8, max = 100, message = "password must be at least 8 characters long, max 100")
        @Pattern(
                regexp = "^[a-zA-Z0-9_а-яА-ЯёЁ@#$%^&+=!()]*$",
                message = "Password contains invalid characters"
        )
        String password
) {
}
