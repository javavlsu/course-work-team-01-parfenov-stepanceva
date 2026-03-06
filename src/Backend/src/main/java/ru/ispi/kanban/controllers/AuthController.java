package ru.ispi.kanban.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.AuthTokensDTO;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.payload.LoginPayload;
import ru.ispi.kanban.payload.RegistrationPayload;
import ru.ispi.kanban.security.JwtProperties;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.util.CookiesHelper;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final HttpServletResponse httpServletResponse;

    private final UserService userService;

    private final CookiesHelper cookies;

    private final AuthService authService;

    private final JwtProperties jwtProperties;

    @PostMapping("login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginPayload loginPayload, HttpServletResponse httpServletResponse){

        AuthTokensDTO tokens = authService.login(loginPayload);

        cookies.setCookie(httpServletResponse, "accessTokenKanban", tokens.getAccessToken(), (int) jwtProperties.accessTime());
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", tokens.getRefreshToken(), (int) jwtProperties.refreshTime());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        userService.getByEmail(loginPayload.email())
                );
    }

    @PostMapping("registration")
    public ResponseEntity<UserDTO> registration(@RequestBody RegistrationPayload registrationPayload, HttpServletResponse response){

        AuthTokensDTO tokens = authService.register(registrationPayload);

        cookies.setCookie(response, "accessTokenKanban",
                tokens.getAccessToken(), (int) jwtProperties.accessTime());

        cookies.setCookie(response, "refreshTokenKanban",
                tokens.getRefreshToken(), (int) jwtProperties.refreshTime());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.getByEmail(registrationPayload.email())
                );
    }

    @GetMapping("checkAuth")
    public ResponseEntity<UserDTO> checkAuth(@CookieValue(value = "accessTokenKanban", required = false) String accessToken){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.checkAuth(accessToken));
    }

    @PostMapping("refresh")
    public ResponseEntity<UserDTO> refresh(@CookieValue(value = "refreshTokenKanban", required = false) String refreshToken){
        String newAccessToken = authService.refresh(refreshToken);

        cookies.setCookie(httpServletResponse,
                "accessTokenKanban",
                newAccessToken,
                (int) jwtProperties.accessTime());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.checkAuth(newAccessToken));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse){
        //стираем куки
        cookies.setCookie(httpServletResponse, "accessTokenKanban", "", 0);
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", "", 0);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
