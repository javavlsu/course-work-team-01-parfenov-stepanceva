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
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.util.ApiResponses;
import ru.ispi.kanban.util.CookiesHelper;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final HttpServletResponse httpServletResponse;

    private final UserService userService;

    private final CookiesHelper cookies;

    private final AuthService authService;

    @Value("${spring.security.jwt.access-time}")
    private int ACCESS_TOKEN_EXPIRATION;

    @Value("${spring.security.jwt.refresh-time}")
    private int REFRESH_TOKEN_EXPIRATION;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginPayload loginPayload, HttpServletResponse httpServletResponse){

        AuthTokensDTO tokens = authService.login(loginPayload);

        cookies.setCookie(httpServletResponse, "accessTokenKanban", tokens.getAccessToken(), ACCESS_TOKEN_EXPIRATION);
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", tokens.getRefreshToken(), REFRESH_TOKEN_EXPIRATION);

        return ResponseEntity
                .status(200)
                .body(ApiResponses.ok(
                        "Login successful",
                        userService.getByEmail(loginPayload.email())
                ));
    }

    @PostMapping("registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationPayload registrationPayload, HttpServletResponse response){

        AuthTokensDTO tokens = authService.register(registrationPayload);

        cookies.setCookie(response, "accessTokenKanban",
                tokens.getAccessToken(), ACCESS_TOKEN_EXPIRATION);

        cookies.setCookie(response, "refreshTokenKanban",
                tokens.getRefreshToken(), REFRESH_TOKEN_EXPIRATION);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponses.ok(
                        "The user has been registered",
                        userService.getByEmail(registrationPayload.email())
                ));
    }

    @GetMapping("checkAuth")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "accessTokenKanban", required = false) String accessToken){

        UserDTO user = authService.checkAuth(accessToken);

        return ResponseEntity.ok(
                    ApiResponses.ok("Authorized", user)
            );
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshTokenKanban", required = false) String refreshToken){
        String newAccessToken = authService.refresh(refreshToken);

        cookies.setCookie(httpServletResponse,
                "accessTokenKanban",
                newAccessToken,
                ACCESS_TOKEN_EXPIRATION);

        return ResponseEntity.ok(
                ApiResponses.ok("Token refreshed", null)
        );
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse){
        //стираем куки
        cookies.setCookie(httpServletResponse, "accessTokenKanban", "", 0);
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", "", 0);
        return ResponseEntity.status(200)
                .body(ApiResponses.ok("Logged out", null));
    }
}
