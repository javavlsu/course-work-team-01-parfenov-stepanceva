package ru.ispi.kanban.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.AuthTokensDTO;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.payload.LoginPayload;
import ru.ispi.kanban.payload.RegistrationPayload;
import ru.ispi.kanban.security.CustomUserDetailsService;
import ru.ispi.kanban.security.jwt.JwtService;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.UserService;
import ru.ispi.kanban.util.CookiesHelper;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final HttpServletResponse httpServletResponse;

    private final UserService userService;

    private final CookiesHelper cookies;
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginPayload loginPayload, HttpServletResponse httpServletResponse){

        AuthTokensDTO tokens = authService.login(loginPayload);

        cookies.setCookie(httpServletResponse, "accessTokenKanban", tokens.getAccessToken(), 15 * 60);
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", tokens.getRefreshToken(), 60 * 60);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationPayload registrationPayload, HttpServletResponse response){

        AuthTokensDTO tokens = authService.register(registrationPayload);

        cookies.setCookie(response, "accessTokenKanban",
                tokens.getAccessToken(), 15 * 60);

        cookies.setCookie(response, "refreshTokenKanban",
                tokens.getRefreshToken(), 60 * 60);

        return ResponseEntity.status(HttpStatus.CREATED).body("Создан пользователь!");
    }

    @GetMapping("checkAuth")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "accessTokenKanban", required = false) String accessToken){
        if(accessToken != null){
            String email = jwtService.extractUsername(accessToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(accessToken, userDetails)){
                return ResponseEntity.ok("Ты авторизован " + email);
            }
            else {
                return ResponseEntity.status(403).body("token dead");
            }
        }
        else{
            return ResponseEntity.status(403).body("Cookie was not found!");
        }
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshTokenKanban", required = false) String refreshToken){
        if( refreshToken == null || !jwtService.isTokenSignatureValid(refreshToken)){
            return  ResponseEntity.status(403).body("Invalid refresh token");
        }

        String email = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtService.isTokenValid(refreshToken, userDetails)){
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            cookies.setCookie(httpServletResponse, "accessTokenKanban", newAccessToken, 15 * 60);
            return ResponseEntity.ok("Token refreshed");
        }

        return ResponseEntity.status(403).body("Token expired");
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse){
        //стираем куки
        cookies.setCookie(httpServletResponse, "accessTokenKanban", "", 0);
        cookies.setCookie(httpServletResponse, "refreshTokenKanban", "", 0);
        return ResponseEntity.ok("Logged out");
    }
}
