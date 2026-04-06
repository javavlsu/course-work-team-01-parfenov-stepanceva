package ru.ispi.kanban.mappers;

import org.mapstruct.*;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.UpdateTaskPayload;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "columnId", source = "column.id")
    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Task toEntity(CreateTaskPayload payload);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Task task, UpdateTaskPayload payload);
}
