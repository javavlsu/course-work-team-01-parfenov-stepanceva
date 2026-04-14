package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.TaskHistoryDto;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.TaskHistory;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.ActionType;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.mappers.TaskHistoryMapper;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.TaskHistoryRepository;
import ru.ispi.kanban.services.TaskHistoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskHistoryMapper taskHistoryMapper;
    private final BoardUserRepository boardUserRepository;

    @Override
    public List<TaskHistoryDto> getHistoryByTask(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);

        return taskHistoryRepository.findAllByTask_IdAndTask_Column_Board_IdOrderByChangedAtDesc(taskId, boardId)
                .stream()
                .map(taskHistoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void createHistoryRecord(Task task, User user, ActionType actionType,
                                    String changedAttribute, String oldValue, String newValue) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setUser(user);
        history.setActionType(actionType);
        history.setChangedAttribute(changedAttribute);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);

        taskHistoryRepository.save(history);
    }

    private void checkAccess(Integer userId, Integer boardId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new BoardAccessDeniedException("No access to this board");
        }
    }
}
