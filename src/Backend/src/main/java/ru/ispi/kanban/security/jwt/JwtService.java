package ru.ispi.kanban.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.security.CustomUserDetailsService;
import ru.ispi.kanban.security.JwtProperties;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(UserDetails userDetails) {
        // Создаем пустую Map для доп. данных, если они не нужны сейчас
        return buildToken(new HashMap<>(), userDetails, jwtProperties.accessTime());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtProperties.refreshTime());
    }

    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails, long tokenExpiration) {

        extraClaims.put("userId", ((CustomUserDetails) userDetails).getId());

        return Jwts.builder()
                .setClaims(extraClaims) // Устанавливаем кастомные пары ключ-значение
                .setSubject(userDetails.getUsername()) // у нас это email пользователя
                .setIssuedAt(new Date(System.currentTimeMillis())) // Время создания
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Подпись ключом алгоритмом HS256
                .compact(); // Склеиваем всё в одну строку Base64
    }

    private Key getSignInKey() {
        // Превращаем текстовый секрет из конфига обратно в байтовый массив
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey());
        // Генерируем ключ, пригодный именно для алгоритма HMAC
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token) // если токен изменен или просрочен - тут вылетит ошибка
                .getBody(); // Если всё ок, достаем данные
    }

    /**
     * Универсальный метод для вытаскивания любого поля из токена.
     * Мы передаем функцию (метод из класса Claims), которая говорит, что именно достать.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        // Проверяем: дата истечения токена раньше, чем текущий момент времени?
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Полная проверка: имя в токене совпадает с пользователем в БД И токен не просрочен
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return true; // подпись корректна, токен просто истёк
        } catch (JwtException | IllegalArgumentException e) {
            return false; // токен подделан или повреждён
        }
    }

    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }
}