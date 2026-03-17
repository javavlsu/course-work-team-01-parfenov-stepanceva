package ru.ispi.kanban.payload;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationPayload(

        @NotBlank(message = "email must not be empty")
        @Email(message = "email should be valid")
        String email,

        @NotBlank(message = "name must not be empty")
        @Pattern(
                regexp = "^[a-zA-Z0-9_а-яА-ЯёЁ]{3,20}$",
                message = "Nickname can only contain letters (Eng/Rus), digits and underscore (3-20 chars)"
        )
        String name,

        @NotBlank(message = "password must not be empty ")
        @Size(min = 8, max = 100, message = "password must be at least 8 characters long, max 100")
        @Pattern(
                regexp = "^[a-zA-Z0-9_а-яА-ЯёЁ@#$%^&+=!()]*$",
                message = "Password contains invalid characters"
        )
        String password
) {
}
