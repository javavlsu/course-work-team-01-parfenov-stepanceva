package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ispi.kanban.entities.Attachment;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    List<Attachment> findAllByTaskIdOrderByUploadedAtDesc(Integer taskId);

    Optional<Attachment> findByIdAndTask_IdAndTask_Column_Board_Id(Integer attachmentId, Integer taskId, Integer boardId);

}