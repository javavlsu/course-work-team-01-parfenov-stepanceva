package ru.ispi.kanban.services;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.AuthTokensDTO;
import ru.ispi.kanban.payload.LoginPayload;
import ru.ispi.kanban.payload.RegistrationPayload;
import ru.ispi.kanban.security.jwt.JwtService;
import ru.ispi.kanban.util.CookiesHelper;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final UserService userService;

    public AuthTokensDTO login (LoginPayload loginPayload){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginPayload.email(), loginPayload.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginPayload.email());

        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthTokensDTO(accessToken, refreshToken);
    }

    public AuthTokensDTO register(RegistrationPayload payload) {

        userService.create(payload);

        // авто-логин после регистрации
        return login(new LoginPayload(
                payload.email(),
                payload.password()
        ));
    }
}
