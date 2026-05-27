package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.enums.ActionType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryDto {

    private Integer id;

    private Integer taskId;

    private UserDto user;

    private ActionType actionType;

    private String changedAttribute;

    private String oldValue;

    private String newValue;

    private LocalDateTime changedAt;
}