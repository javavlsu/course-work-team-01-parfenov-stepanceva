package ru.ispi.kanban.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ispi.kanban.dto.TaskHistoryDto;
import ru.ispi.kanban.entities.TaskHistory;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskHistoryMapper {

    @Mapping(source = "task.id", target = "taskId")
    TaskHistoryDto toDto(TaskHistory entity);
}