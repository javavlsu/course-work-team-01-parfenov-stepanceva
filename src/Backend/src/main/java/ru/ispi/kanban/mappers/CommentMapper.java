package ru.ispi.kanban.mappers;


import org.mapstruct.*;
import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.entities.Comment;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CreateCommentPayload payload);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Comment comment, UpdateCommentPayload payload);
}
