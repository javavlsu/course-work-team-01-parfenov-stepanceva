package ru.ispi.kanban.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.AuthTokensDto;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.payloads.LoginPayload;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.security.JwtProperties;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.utils.CookiesHelper;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final HttpServletResponse httpServletResponse;

    private final UserService userService;

    private final AuthService authService;

    private final JwtProperties jwtProperties;

    @PostMapping("login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginPayload loginPayload, HttpServletResponse httpServletResponse){

        AuthTokensDto tokens = authService.login(loginPayload);

        CookiesHelper.setCookie(httpServletResponse, "accessTokenKanban", tokens.getAccessToken(), (int) jwtProperties.accessTime());
        CookiesHelper.setCookie(httpServletResponse, "refreshTokenKanban", tokens.getRefreshToken(), (int) jwtProperties.refreshTime());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        userService.getByEmail(loginPayload.email())
                );
    }

    @PostMapping("registration")
    public ResponseEntity<UserDto> registration(@Valid @RequestBody RegistrationPayload registrationPayload, HttpServletResponse response){

        AuthTokensDto tokens = authService.register(registrationPayload);

        CookiesHelper.setCookie(response, "accessTokenKanban",
                tokens.getAccessToken(), (int) jwtProperties.accessTime());

        CookiesHelper.setCookie(response, "refreshTokenKanban",
                tokens.getRefreshToken(), (int) jwtProperties.refreshTime());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.getByEmail(registrationPayload.email())
                );
    }

    @GetMapping("checkAuth")
    public ResponseEntity<UserDto> checkAuth(@CookieValue(value = "accessTokenKanban", required = false) String accessToken){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.checkAuth(accessToken));
    }

    @PostMapping("refresh")
    public ResponseEntity<UserDto> refresh(@CookieValue(value = "refreshTokenKanban", required = false) String refreshToken){
        String newAccessToken = authService.refresh(refreshToken);

        CookiesHelper.setCookie(httpServletResponse,
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
        CookiesHelper.setCookie(httpServletResponse, "accessTokenKanban", "", 0);
        CookiesHelper.setCookie(httpServletResponse, "refreshTokenKanban", "", 0);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
