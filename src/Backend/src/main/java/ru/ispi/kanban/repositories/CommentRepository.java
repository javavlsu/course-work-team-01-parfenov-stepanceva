package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Получить все комментарии к задаче (сортировка от старых к новым)
    List<Comment> findAllByTaskIdOrderByCreatedAtAsc(Integer taskId);

    // Безопасный поиск комментария с проверкой принадлежности к задаче и доске
    Optional<Comment> findByIdAndTask_IdAndTask_Column_Board_Id(Integer commentId, Integer taskId, Integer boardId);
}