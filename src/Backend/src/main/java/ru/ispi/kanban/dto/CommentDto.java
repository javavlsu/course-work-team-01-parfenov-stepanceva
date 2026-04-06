package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private Integer taskId; // Отдаем только id задачи
    private UserDto user;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}