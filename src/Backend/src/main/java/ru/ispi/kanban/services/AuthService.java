package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.AuthTokensDto;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.payloads.LoginPayload;
import ru.ispi.kanban.payloads.RegistrationPayload;

public interface AuthService {

    AuthTokensDto login(LoginPayload loginPayload);

    AuthTokensDto register(RegistrationPayload payload);

    UserDto checkAuth(String accessToken);

    String refresh(String refreshToken);

    Integer getUserIdFromToken(String accessToken);
}
