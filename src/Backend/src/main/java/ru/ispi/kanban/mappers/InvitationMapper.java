package ru.ispi.kanban.mappers;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.entities.Invitation;

@Mapper(componentModel = "spring", uses = {GroupTeamMapper.class, UserMapper.class})
public interface InvitationMapper {

    InvitationDto toDto(Invitation invitation);
}
