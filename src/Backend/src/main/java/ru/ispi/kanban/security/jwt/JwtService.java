package ru.ispi.kanban.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${spring.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${spring.security.jwt.access-time}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${spring.security.jwt.refresh-time}")
    private long REFRESH_TOKEN_EXPIRATION;

    public String generateAccessToken(UserDetails userDetails) {
        // Создаем пустую Map для доп. данных, если они не нужны сейчас
        return buildToken(new HashMap<>(), userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * @param extraClaims - дополнительные данные (роли, id)
     * @param userDetails - данные пользователя из Spring Security
     * @param tokenExpiration - время жизни в миллисекундах
     */
    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails, long tokenExpiration) {
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
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
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
            // Пытаемся просто распарсить токен. Если подпись кривая - упадет в Catch.
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Токен подделан или поврежден
        }
    }
}