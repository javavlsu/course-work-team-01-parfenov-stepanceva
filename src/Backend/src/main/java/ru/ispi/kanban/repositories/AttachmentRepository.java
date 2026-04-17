package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Attachment;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    // user нужен для AttachmentDto.user
    @EntityGraph(attributePaths = "user")
    List<Attachment> findAllByTaskIdOrderByUploadedAtDesc(Integer taskId);

    // user и task нужны для маппинга и событий при удалении
    @EntityGraph(attributePaths = {"user", "task"})
    Optional<Attachment> findByIdAndTask_IdAndTask_Column_Board_Id(Integer attachmentId, Integer taskId, Integer boardId);

    void deleteAllByTask_Id(Integer taskId);
}
