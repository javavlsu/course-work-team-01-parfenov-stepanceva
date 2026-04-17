package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.AuthTokensDto;
import ru.ispi.kanban.exceptions.ApiException;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.payloads.LoginPayload;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.security.jwt.JwtService;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.UserService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public AuthTokensDto login(LoginPayload loginPayload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginPayload.email(), loginPayload.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginPayload.email());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthTokensDto(accessToken, refreshToken);
    }

    @Override
    public AuthTokensDto register(RegistrationPayload payload) {
        userService.create(payload);

        // авто-логин после регистрации
        return login(new LoginPayload(payload.email(), payload.password()));
    }

    @Override
    public UserDto checkAuth(String accessToken) {
        if (accessToken == null) {
            throw new RuntimeException("Cookie not found");
        }

        String email = jwtService.extractUsername(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(accessToken, userDetails)) {
            throw new RuntimeException("Token expired");
        }

        return userService.getByEmail(email);
    }

    @Override
    public String refresh(String refreshToken) {
        if (refreshToken == null || !jwtService.isTokenSignatureValid(refreshToken)) {
            throw new ApiException("Refresh token is invalid", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new ApiException("Refresh token expired, please log in again", HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateAccessToken(userDetails);
    }

    @Override
    public Integer getUserIdFromToken(String accessToken) {
        if (accessToken == null) {
            throw new RuntimeException("Cookie not found");
        }

        String email = jwtService.extractUsername(accessToken);
        UserDto user = userService.getByEmail(email);

        return user.getId();
    }
}
