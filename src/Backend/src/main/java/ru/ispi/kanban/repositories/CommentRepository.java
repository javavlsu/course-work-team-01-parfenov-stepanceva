package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // user нужен для CommentDto.user, task — для taskId в ответе
    @EntityGraph(attributePaths = "user")
    List<Comment> findAllByTaskIdOrderByCreatedAtAsc(Integer taskId);

    // user и task нужны для маппинга; task также используется при публикации событий
    @EntityGraph(attributePaths = {"user", "task"})
    Optional<Comment> findByIdAndTask_IdAndTask_Column_Board_Id(Integer commentId, Integer taskId, Integer boardId);

    void deleteAllByTask_Id(Integer taskId);

    void deleteAllByTask_ColumnId(Integer columnId);

    void deleteAllByTask_Column_Board_Id(Integer boardId);
}
