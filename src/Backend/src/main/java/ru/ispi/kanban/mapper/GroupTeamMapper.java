package ru.ispi.kanban.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.payload.GroupTeamPayload;

@Mapper(componentModel = "spring")
public interface GroupTeamMapper {
    GroupTeamDto toDto(GroupTeam group);

    GroupTeam toEntity(GroupTeamPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget GroupTeam group, GroupTeamPayload payload);
}
