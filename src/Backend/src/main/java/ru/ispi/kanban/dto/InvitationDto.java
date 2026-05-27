package ru.ispi.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ispi.kanban.enums.InvitationStatus;
import ru.ispi.kanban.enums.InvitationType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDto {

    private Integer id;

    private GroupTeamDto group;

    //будет нал если для доступа по ссылке
    private String email;

    private InvitationType type;

    private InvitationStatus status;

    private String inviteToken;

    private UserDto createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
}
