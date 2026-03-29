package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}