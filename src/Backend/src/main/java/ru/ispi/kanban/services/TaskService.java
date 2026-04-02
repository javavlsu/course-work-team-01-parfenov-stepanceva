package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.mappers.TaskMapper;
import ru.ispi.kanban.repositories.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public List<TaskDto> GetTasks(Integer id, Integer boardId) {
        return taskRepository.findAllByColumn_Board_Id(boardId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }
}
