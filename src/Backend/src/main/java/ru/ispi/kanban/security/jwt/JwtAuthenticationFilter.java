package ru.ispi.kanban.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwtToken = null; // Изначально пустой токен


        //Идем по кукам из запроса, если есть наш сохраненный кук accessTokenKanban, то тогда выходим из этого цикла
        if (request.getCookies() != null){
            for (Cookie cookie: request.getCookies()){
                if ("accessTokenKanban".equals(cookie.getName())){
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        //если не найдет то
        if (jwtToken == null){
            filterChain.doFilter(request, response); // говорим фильтру что пусть идет своей дорогой сталкер
            return;
        }

        //теперь попытаемся вытянуть почту из токена

        String userEmail = null;
        try {
            userEmail = jwtService.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            // токен протух
            logger.warn("JWT token is expired");
            filterChain.doFilter(request, response);
            return;
        } catch (JwtException | IllegalArgumentException e) {
            // остальные ошибки парсинга
            logger.error("JWT token is invalid", e);
            filterChain.doFilter(request, response);
            return;
        }


        // Если email извлечен и пользователь еще не аутентифицирован в текущем контексте
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() ==  null){

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Добавляем дополнительные детали запроса (например, IP, ID сессии)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем пользователя как аутентифицированного в контексте безопасности Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }

        filterChain.doFilter(request, response);
    }
}
