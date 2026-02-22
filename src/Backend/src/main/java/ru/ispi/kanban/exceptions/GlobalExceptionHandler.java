package ru.ispi.kanban.exceptions;


import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ispi.kanban.dto.ApiResponse;
import ru.ispi.kanban.util.ApiResponses;

@RestControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponses.error(ex.getMessage()));
    }
}
