package ru.ispi.kanban.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.entities.GroupMember;

@Mapper(componentModel = "spring")
public interface GroupMemberMapper {

    @Mapping(source = "id.groupId", target = "groupId")
    @Mapping(source = "id.userId", target = "userId")
    GroupMemberDto toDto(GroupMember member);
}
