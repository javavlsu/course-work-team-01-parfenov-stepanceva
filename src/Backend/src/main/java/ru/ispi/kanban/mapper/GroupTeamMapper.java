package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entity.GroupTeam;

@Mapper(componentModel = "spring")
public interface GroupTeamMapper {
    GroupTeamDto toDto(GroupTeam group);
}
