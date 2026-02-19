package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;   // ok | error

    private String details;  // описание

    private T data;          // payload
}
