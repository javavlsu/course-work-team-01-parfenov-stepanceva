package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.TaskHistory;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Integer> {

    // user нужен для TaskHistoryDto.user, task — для taskId
    @EntityGraph(attributePaths = {"user", "task"})
    List<TaskHistory> findAllByTask_IdAndTask_Column_Board_IdOrderByChangedAtDesc(Integer taskId, Integer boardId);
}
