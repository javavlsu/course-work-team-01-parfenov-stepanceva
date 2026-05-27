package ru.ispi.kanban.services;

import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.AttachmentDto;

import java.util.List;

public interface AttachmentService {

    List<AttachmentDto> getAttachmentsByTask(Integer userId, Integer boardId, Integer taskId);

    AttachmentDto upload(Integer userId, Integer boardId, Integer taskId, MultipartFile file);

    void delete(Integer userId, Integer boardId, Integer taskId, Integer attachmentId);
}
