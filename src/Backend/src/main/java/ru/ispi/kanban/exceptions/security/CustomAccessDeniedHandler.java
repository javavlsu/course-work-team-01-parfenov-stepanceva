package ru.ispi.kanban.exceptions.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.ispi.kanban.exceptions.ErrorCode;
import ru.ispi.kanban.exceptions.ErrorMessages;
import ru.ispi.kanban.exceptions.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(
                response.getWriter(),
                ErrorResponse.of(ErrorCode.AUTH_FORBIDDEN, ErrorMessages.of(ErrorCode.AUTH_FORBIDDEN))
        );
    }
}
