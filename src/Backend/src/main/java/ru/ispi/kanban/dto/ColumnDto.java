package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.entities.Board;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDto {

    private Integer id;

    private Integer boardId;

    private String title;

    private Long position;

    private LocalDateTime createdAt;

}
