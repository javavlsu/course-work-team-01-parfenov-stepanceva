package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByColumn_Board_Id(Integer boardId);

    List<Task> findAllByColumnIdOrderByPositionAsc(Integer columnId);

    List<Task> findAllByAssigneeIdAndColumn_Board_Id(Integer assigneeId, Integer boardId);

    Optional<Task> findByIdAndColumn_Board_Id(Integer taskId, Integer boardId);
}
