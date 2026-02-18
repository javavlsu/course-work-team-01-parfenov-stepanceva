package ru.ispi.kanban.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.payload.LoginPayload;
import ru.ispi.kanban.security.CustomUserDetailsService;
import ru.ispi.kanban.security.jwt.JwtService;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService userDetailsService;

    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginPayload loginPayload, HttpServletResponse httpServletResponse){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginPayload.email(), loginPayload.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginPayload.email());

        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        setCookie(httpServletResponse, "accessTokenKanban", accessToken, 15 * 60);
        setCookie(httpServletResponse, "refreshTokenKanban", refreshToken, 60 * 60);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse){
        //стираем куки
        setCookie(httpServletResponse, "accessTokenKanban", "", 0);
        setCookie(httpServletResponse, "refreshTokenKanban", "", 0);
        return ResponseEntity.ok("Logged out");
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
            setCookie(httpServletResponse, "accessTokenKanban", newAccessToken, 15 * 60);
            return ResponseEntity.ok("Token refreshed");
        }

        return ResponseEntity.status(403).body("Token expired");
    }

    private void setCookie(HttpServletResponse response, String nameCookie, String valueCookie, int maxAge){
        ResponseCookie cookie = ResponseCookie.from(nameCookie, valueCookie)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
