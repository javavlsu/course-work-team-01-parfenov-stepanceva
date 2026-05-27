package ru.ispi.kanban.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.payloads.GroupTeamPayload;

@Mapper(componentModel = "spring")
public interface GroupTeamMapper {
    GroupTeamDto toDto(GroupTeam group);

    GroupTeam toEntity(GroupTeamPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget GroupTeam group, GroupTeamPayload payload);
}
