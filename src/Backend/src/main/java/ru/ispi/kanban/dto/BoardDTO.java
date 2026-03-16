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
public class BoardDTO {

    private Integer id;

    private GroupTeamDTO group;

    private String title;

    private String description;

    private UserDTO createdBy;

    private LocalDateTime createdAt;

}
