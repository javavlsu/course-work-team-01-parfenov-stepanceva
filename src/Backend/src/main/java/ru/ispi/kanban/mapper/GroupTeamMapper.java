package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.GroupTeamDTO;
import ru.ispi.kanban.entity.GroupTeam;

@Mapper(componentModel = "spring")
public interface GroupTeamMapper {
    GroupTeamDTO toDto(GroupTeam group);
}
