package ru.ispi.kanban.security;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.security.jwt")
public record JwtProperties(
        // @Value("${spring.security.jwt.secret-key}")
        String secretKey,

        // @Value("${spring.security.jwt.access-time}")
        long accessTime,

        // @Value("${spring.security.jwt.refresh-time}")
        long refreshTime
) {
}
