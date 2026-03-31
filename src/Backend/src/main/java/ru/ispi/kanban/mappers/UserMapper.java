package ru.ispi.kanban.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.payloads.RegistrationPayload;
import ru.ispi.kanban.payloads.UserPayload;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(RegistrationPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    void update(@MappingTarget User user, UserPayload payload);
}