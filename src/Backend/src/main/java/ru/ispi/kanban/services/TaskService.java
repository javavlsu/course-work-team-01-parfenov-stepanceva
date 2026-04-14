package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.UpdateTaskPayload;

import java.util.List;

public interface TaskService {

    List<TaskDto> getTasksByBoard(Integer userId, Integer boardId);

    TaskDto getTask(Integer userId, Integer boardId, Integer taskId);

    List<TaskDto> getTasksByColumn(Integer userId, Integer boardId, Integer columnId);

    List<TaskDto> getTasksByAssignee(Integer userId, Integer boardId, Integer assigneeId);

    TaskDto create(Integer userId, Integer boardId, CreateTaskPayload payload);

    TaskDto update(Integer userId, Integer boardId, Integer taskId, UpdateTaskPayload payload);

    void delete(Integer userId, Integer boardId, Integer taskId);
}
