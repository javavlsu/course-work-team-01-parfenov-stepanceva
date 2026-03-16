package ru.ispi.kanban.mapper;

import org.mapstruct.Mapper;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
}