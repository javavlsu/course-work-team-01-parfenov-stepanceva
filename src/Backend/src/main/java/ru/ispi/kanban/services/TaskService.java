package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.mappers.TaskMapper;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.UpdateTaskPayload;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.ColumnRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final ColumnRepository columnRepository;

    private final UserRepository userRepository;

    private final BoardUserRepository boardUserRepository;

    private final TaskMapper taskMapper;

    public List<TaskDto> getTasksByBoard(Integer userId, Integer boardId) {
        checkAccess(userId, boardId);

        return taskRepository.findAllByColumn_Board_Id(boardId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public TaskDto getTask(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);

        return taskMapper.toDto(getTaskEntity(boardId, taskId));
    }

    public List<TaskDto> getTasksByColumn(Integer userId, Integer boardId, Integer columnId) {
        checkAccess(userId, boardId);

        getColumn(boardId, columnId);

        return taskRepository.findAllByColumnIdOrderByPositionAsc(columnId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public List<TaskDto> getTasksByAssignee(Integer userId, Integer boardId, Integer assigneeId) {
        checkAccess(userId, boardId);

        return taskRepository.findAllByAssigneeIdAndColumn_Board_Id(assigneeId, boardId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional
    public TaskDto create(Integer userId, Integer boardId, CreateTaskPayload payload) {
        checkAccess(userId, boardId);

        Task task = taskMapper.toEntity(payload);
        task.setColumn(getColumn(boardId, payload.columnId()));
        task.setAssignee(resolveUser(payload.assigneeId()));

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto update(Integer userId, Integer boardId, Integer taskId, UpdateTaskPayload payload) {
        checkAccess(userId, boardId);

        Task task = getTaskEntity(boardId, taskId);
        taskMapper.update(task, payload);

        if (payload.columnId() != null) {
            task.setColumn(getColumn(boardId, payload.columnId()));
        }
        if (payload.assigneeId() != null) {
            task.setAssignee(resolveUser(payload.assigneeId()));
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void delete(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);

        taskRepository.delete(getTaskEntity(boardId, taskId));
    }

    private void checkAccess(Integer userId, Integer boardId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new RuntimeException("Нет доступа к доске");
        }
    }

    private BoardColumn getColumn(Integer boardId, Integer columnId) {
        return columnRepository.findByIdAndBoardId(columnId, boardId)
                .orElseThrow(() -> new RuntimeException("Колонка не найдена"));
    }

    private Task getTaskEntity(Integer boardId, Integer taskId) {
        return taskRepository.findByIdAndColumn_Board_Id(taskId, boardId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    private User resolveUser(Integer userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
