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
public class GroupTeamDTO {
    Integer id;
    String name;
    String description;
    LocalDateTime createdAt;
}
