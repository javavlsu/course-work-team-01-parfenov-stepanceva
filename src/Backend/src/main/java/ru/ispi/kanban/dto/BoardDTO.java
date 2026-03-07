package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Integer id;

    private GroupTeam group;

    private String title;

    private String description;

    private User createdBy;

    private LocalDateTime createdAt;

}
