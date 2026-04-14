package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.TaskHistoryDto;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.ActionType;

import java.util.List;

public interface TaskHistoryService {

    List<TaskHistoryDto> getHistoryByTask(Integer userId, Integer boardId, Integer taskId);

    void createHistoryRecord(Task task, User user, ActionType actionType,
                             String changedAttribute, String oldValue, String newValue);
}
