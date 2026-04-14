package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.AttachmentDto;
import ru.ispi.kanban.entities.Attachment;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.ActionType;
import ru.ispi.kanban.exceptions.AttachmentNotFoundException;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.exceptions.TaskNotFoundException;
import ru.ispi.kanban.listeners.TaskChangeEvent;
import ru.ispi.kanban.mappers.AttachmentMapper;
import ru.ispi.kanban.repositories.AttachmentRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.AttachmentService;
import ru.ispi.kanban.services.FileStorageService;

import java.util.List;

import static ru.ispi.kanban.constants.StorageConstants.FOLDER_ATTACHMENTS;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BoardUserRepository boardUserRepository;
    private final AttachmentMapper attachmentMapper;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<AttachmentDto> getAttachmentsByTask(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);
        return attachmentRepository.findAllByTaskIdOrderByUploadedAtDesc(taskId)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AttachmentDto upload(Integer userId, Integer boardId, Integer taskId, MultipartFile file) {
        checkAccess(userId, boardId);

        String storageKey = fileStorageService.uploadFile(file, FOLDER_ATTACHMENTS);
        Task task = getTask(boardId, taskId);
        User user = getUser(userId);

        Attachment attachment = attachmentMapper.toEntity(
                file.getOriginalFilename(),
                storageKey,
                task,
                user
        );

        Attachment savedAttachment = attachmentRepository.save(attachment);

        // Публикуем событие
        eventPublisher.publishEvent(new TaskChangeEvent(
                task, userId, ActionType.create, "ATTACHMENT", null, file.getOriginalFilename()
        ));

        return attachmentMapper.toDto(savedAttachment);
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer boardId, Integer taskId, Integer attachmentId) {
        checkAccess(userId, boardId);

        Attachment attachment = attachmentRepository.findByIdAndTask_IdAndTask_Column_Board_Id(attachmentId, taskId, boardId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found: " + attachmentId));

        // task уже загружен через EntityGraph в findByIdAndTask_IdAndTask_Column_Board_Id
        eventPublisher.publishEvent(new TaskChangeEvent(
                attachment.getTask(), userId, ActionType.delete, "ATTACHMENT", attachment.getFileName(), null
        ));

        fileStorageService.deleteFile(attachment.getStorageKey());
        attachmentRepository.delete(attachment);
    }

    private void checkAccess(Integer userId, Integer boardId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new BoardAccessDeniedException("No access to this board");
        }
    }

    private Task getTask(Integer boardId, Integer taskId) {
        return taskRepository.findByIdAndColumn_Board_Id(taskId, boardId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + taskId));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserByIdException("User not found: " + userId));
    }
}
