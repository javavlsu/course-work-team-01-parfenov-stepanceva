package ru.ispi.kanban.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ispi.kanban.dto.AttachmentDto;
import ru.ispi.kanban.entities.Attachment;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(target = "taskId", source = "task.id")
    AttachmentDto toDto(Attachment attachment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    Attachment toEntity(String fileName, String storageKey, Task task, User user);
}