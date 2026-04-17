package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @EntityGraph(attributePaths = {"column", "assignee"})
    List<Task> findAllByColumn_Board_Id(Integer boardId);

    @EntityGraph(attributePaths = {"column", "assignee"})
    List<Task> findAllByColumnIdOrderByPositionAsc(Integer columnId);

    @EntityGraph(attributePaths = {"column", "assignee"})
    List<Task> findAllByAssigneeIdAndColumn_Board_Id(Integer assigneeId, Integer boardId);

    @EntityGraph(attributePaths = {"column", "assignee"})
    Optional<Task> findByIdAndColumn_Board_Id(Integer taskId, Integer boardId);

    @Query("SELECT COALESCE(MAX(t.position), 0) FROM Task t WHERE t.column.id = :columnId")
    Long findMaxPosition(Integer columnId);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 WHERE t.column.id = :columnId AND t.position > :oldPos AND t.position <= :newPos")
    void shiftForward(Integer columnId, Long oldPos, Long newPos);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 WHERE t.column.id = :columnId AND t.position >= :newPos AND t.position < :oldPos")
    void shiftBackward(Integer columnId, Long oldPos, Long newPos);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position - 1 WHERE t.column.id = :columnId AND t.position > :deletedPos")
    void shiftAfterDelete(Integer columnId, Long deletedPos);

    @Modifying
    @Query("UPDATE Task t SET t.position = t.position + 1 WHERE t.column.id = :columnId AND t.position >= :newPos")
    void shiftAfterInsert(Integer columnId, Long newPos);

    void deleteAllByColumnId(Integer columnId);

    void deleteAllByColumn_Board_Id(Integer boardId);
}