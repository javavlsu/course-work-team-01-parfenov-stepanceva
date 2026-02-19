package ru.ispi.kanban.util;

import ru.ispi.kanban.dto.ApiResponse;

public class ApiResponses {

    public static <T> ApiResponse<T> ok(String details, T data) {
        return ApiResponse.<T>builder()
                .status("ok")
                .details(details)
                .data(data)
                .build();
    }

    public static ApiResponse<?> error(String details) {
        return ApiResponse.builder()
                .status("error")
                .details(details)
                .data(null)
                .build();
    }
}
