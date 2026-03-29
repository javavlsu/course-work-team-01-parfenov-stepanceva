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
public class BoardDto {

    private Integer id;

    private GroupTeamDto group;

    private String title;

    private String description;

    private UserDto createdBy;

    private LocalDateTime createdAt;

}
