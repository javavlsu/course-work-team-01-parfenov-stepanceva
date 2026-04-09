package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.ActionType;
import ru.ispi.kanban.listeners.TaskChangeEvent;
import ru.ispi.kanban.mappers.TaskMapper;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.UpdateTaskPayload;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.ColumnRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final ColumnRepository columnRepository;

    private final UserRepository userRepository;

    private final BoardUserRepository boardUserRepository;

    private final TaskMapper taskMapper;

    private final ApplicationEventPublisher eventPublisher;

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

        Task savedTask = taskRepository.save(task);

        // запись о создании задачи
        eventPublisher.publishEvent(new TaskChangeEvent(
                savedTask, userId, ActionType.create, "TASK", null, "Задача создана"
        ));

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto update(Integer userId, Integer boardId, Integer taskId, UpdateTaskPayload payload) {
        checkAccess(userId, boardId);

        Task task = getTaskEntity(boardId, taskId);

        Task oldTask = copy(task); // снимок



        taskMapper.update(task, payload);

        if (payload.columnId() != null) {
            task.setColumn(getColumn(boardId, payload.columnId()));
        }
        if (payload.assigneeId() != null) {
            task.setAssignee(resolveUser(payload.assigneeId()));
        }

        Task savedTask = taskRepository.save(task);

        publishChanges(oldTask, task, userId, payload);

        return taskMapper.toDto(savedTask);
    }

    private Task copy(Task task) {
        Task copy = new Task();
        copy.setTitle(task.getTitle());
        copy.setDescription(task.getDescription());
        copy.setColumn(task.getColumn());
        copy.setAssignee(task.getAssignee());
        copy.setPosition(task.getPosition());
        copy.setDeadline(task.getDeadline());
        copy.setPriority(task.getPriority());
        copy.setStatus(task.getStatus());
        return copy;
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

    private void publishChanges(Task oldTask, Task newTask,
                                Integer userId, UpdateTaskPayload payload) {

        if (payload.title() != null && !Objects.equals(oldTask.getTitle(), payload.title())) {
            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "TITLE", oldTask.getTitle(), payload.title()
            ));
        }

        if (payload.description() != null && !Objects.equals(oldTask.getDescription(), payload.description())) {
            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "DESCRIPTION", oldTask.getDescription(), payload.description()
            ));
        }

        if (payload.columnId() != null &&
                !Objects.equals(oldTask.getColumn().getId(), payload.columnId())) {

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "COLUMN",
                    String.valueOf(oldTask.getColumn().getId()),
                    String.valueOf(payload.columnId())
            ));
        }

        if (!Objects.equals(
                oldTask.getAssignee() != null ? oldTask.getAssignee().getId() : null,
                payload.assigneeId())) {

            String oldVal = oldTask.getAssignee() != null
                    ? String.valueOf(oldTask.getAssignee().getId())
                    : "Unassigned";

            String newVal = payload.assigneeId() != null
                    ? String.valueOf(payload.assigneeId())
                    : "Unassigned";

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "ASSIGNEE", oldVal, newVal
            ));
        }

        if (payload.position() != null &&
                !Objects.equals(oldTask.getPosition(), payload.position())) {

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "POSITION",
                    String.valueOf(oldTask.getPosition()),
                    String.valueOf(payload.position())
            ));
        }

        if (payload.deadline() != null &&
                !Objects.equals(oldTask.getDeadline(), payload.deadline())) {

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "DEADLINE",
                    String.valueOf(oldTask.getDeadline()),
                    String.valueOf(payload.deadline())
            ));
        }

        if (payload.priority() != null &&
                !Objects.equals(oldTask.getPriority(), payload.priority())) {

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "PRIORITY",
                    String.valueOf(oldTask.getPriority()),
                    String.valueOf(payload.priority())
            ));
        }

        if (payload.status() != null &&
                !Objects.equals(oldTask.getStatus(), payload.status())) {

            eventPublisher.publishEvent(new TaskChangeEvent(
                    newTask, userId, ActionType.update,
                    "STATUS",
                    String.valueOf(oldTask.getStatus()),
                    String.valueOf(payload.status())
            ));
        }
    }
}
