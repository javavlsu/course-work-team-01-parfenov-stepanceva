package ru.ispi.kanban.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookiesHelper {
    public void setCookie(HttpServletResponse response, String nameCookie, String valueCookie, int maxAge){
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
