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
public class GroupMemberDTO {

    private Integer groupId;

    private Integer userId;

    private GroupRole role;

    private LocalDateTime joinedAt;

}
