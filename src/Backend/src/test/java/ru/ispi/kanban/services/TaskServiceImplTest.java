package ru.ispi.kanban.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.entities.BoardColumn;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.exceptions.ColumnNotFoundException;
import ru.ispi.kanban.exceptions.TaskNotFoundException;
import ru.ispi.kanban.mappers.TaskMapper;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.ColumnRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.impl.TaskServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock TaskRepository taskRepository;

    @Mock ColumnRepository columnRepository;

    @Mock BoardUserRepository boardUserRepository;

    @Mock TaskMapper taskMapper;

    @Mock UserRepository userRepository;

    @InjectMocks TaskServiceImpl taskService;

    @Test
    void getTasksByBoard_success() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        Task task = new Task();
        TaskDto dto = new TaskDto();
        when(taskRepository.findAllByColumn_Board_Id(10)).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(dto);

        List<TaskDto> result = taskService.getTasksByBoard(1, 10);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void getTasksByBoard_noAccess_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(false);

        assertThatThrownBy(() -> taskService.getTasksByBoard(1, 10))
                .isInstanceOf(BoardAccessDeniedException.class);

        verify(taskRepository, never()).findAllByColumn_Board_Id(any());
    }

    @Test
    void getTask_success() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        Task task = new Task();
        TaskDto dto = new TaskDto();
        when(taskRepository.findByIdAndColumn_Board_Id(5, 10)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(dto);

        assertThat(taskService.getTask(1, 10, 5)).isSameAs(dto);
    }

    @Test
    void getTask_notFound_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        when(taskRepository.findByIdAndColumn_Board_Id(99, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(1, 10, 99))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void create_success_noAssignee() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        BoardColumn column = new BoardColumn();
        column.setId(3);
        when(columnRepository.findByIdAndBoardId(3, 10)).thenReturn(Optional.of(column));
        Task task = new Task();
        when(taskMapper.toEntity(any())).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        TaskDto dto = new TaskDto();
        when(taskMapper.toDto(task)).thenReturn(dto);

        TaskDto result = taskService.create(1, 10, new CreateTaskPayload(3, "My task", null, null, null, null, null));

        assertThat(result).isSameAs(dto);
        assertThat(task.getPosition()).isEqualTo(1L);
        assertThat(task.getColumn()).isSameAs(column);
        verify(taskRepository).shiftAfterInsert(3, 1L);
    }

    @Test
    void create_noAccess_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(false);

        assertThatThrownBy(() -> taskService.create(1, 10, new CreateTaskPayload(3, "Task", null, null, null, null, null)))
                .isInstanceOf(BoardAccessDeniedException.class);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_columnNotFound_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        when(columnRepository.findByIdAndBoardId(999, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(1, 10, new CreateTaskPayload(999, "Task", null, null, null, null, null)))
                .isInstanceOf(ColumnNotFoundException.class);
    }

    @Test
    void create_withAssignee_success() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        BoardColumn column = new BoardColumn();
        column.setId(3);
        when(columnRepository.findByIdAndBoardId(3, 10)).thenReturn(Optional.of(column));
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 7)).thenReturn(true);
        User assignee = new User();
        assignee.setId(7);
        when(userRepository.findById(7)).thenReturn(Optional.of(assignee));
        Task task = new Task();
        when(taskMapper.toEntity(any())).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(new TaskDto());

        taskService.create(1, 10, new CreateTaskPayload(3, "Task", null, 7, null, null, null));

        assertThat(task.getAssignee()).isSameAs(assignee);
    }

    @Test
    void create_assigneeNotOnBoard_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        BoardColumn column = new BoardColumn();
        column.setId(3);
        when(columnRepository.findByIdAndBoardId(3, 10)).thenReturn(Optional.of(column));
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 7)).thenReturn(false);
        Task task = new Task();
        when(taskMapper.toEntity(any())).thenReturn(task);

        assertThatThrownBy(() -> taskService.create(1, 10, new CreateTaskPayload(3, "Task", null, 7, null, null, null)))
                .isInstanceOf(BoardAccessDeniedException.class);
    }

    @Test
    void delete_success() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        BoardColumn column = new BoardColumn();
        column.setId(3);
        Task task = new Task();
        task.setPosition(2L);
        task.setColumn(column);
        when(taskRepository.findByIdAndColumn_Board_Id(5, 10)).thenReturn(Optional.of(task));

        taskService.delete(1, 10, 5);

        verify(taskRepository).delete(task);
        verify(taskRepository).shiftAfterDelete(3, 2L);
    }

    @Test
    void delete_taskNotFound_throws() {
        when(boardUserRepository.existsByIdBoardIdAndIdUserId(10, 1)).thenReturn(true);
        when(taskRepository.findByIdAndColumn_Board_Id(99, 10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(1, 10, 99))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
