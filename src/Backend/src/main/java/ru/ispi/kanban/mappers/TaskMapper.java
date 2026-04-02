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

    Task toEntity(CreateTaskPayload payload);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Task task, UpdateTaskPayload payload);
}
