package ru.ispi.kanban.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.TaskPriority;
import ru.ispi.kanban.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Integer id;

    private Integer columnId; // вместой полной колонки отдаем айди колонки

    private String title;

    private String description;

    private UserDto assignee; //вместо пользователя отдает дто пользователя

    private Long position;

    private LocalDateTime deadline;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDateTime createdAt;
}
