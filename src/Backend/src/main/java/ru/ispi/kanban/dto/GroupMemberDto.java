package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.enums.GroupRole;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDto {

    private Integer groupId;

    private Integer userId;

    private UserDto user;

    private GroupRole role;

    private LocalDateTime joinedAt;

}
