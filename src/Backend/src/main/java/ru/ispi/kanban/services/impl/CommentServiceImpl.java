package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.entities.Comment;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.ActionType;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.exceptions.CommentAccessDeniedException;
import ru.ispi.kanban.exceptions.CommentNotFoundException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.exceptions.TaskNotFoundException;
import ru.ispi.kanban.listeners.TaskChangeEvent;
import ru.ispi.kanban.mappers.CommentMapper;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.CommentRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BoardUserRepository boardUserRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<CommentDto> getCommentsByTask(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);
        return commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(Integer userId, Integer boardId, Integer taskId, CreateCommentPayload payload) {
        checkAccess(userId, boardId);

        Task task = getTask(boardId, taskId);
        Comment comment = commentMapper.toEntity(payload);
        comment.setTask(task);
        comment.setUser(getUser(userId));

        Comment savedComment = commentRepository.save(comment);

        eventPublisher.publishEvent(new TaskChangeEvent(
                task, userId, ActionType.create, "COMMENT", null, payload.text()
        ));

        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto update(Integer userId, Integer boardId, Integer taskId, Integer commentId, UpdateCommentPayload payload) {
        checkAccess(userId, boardId);

        Comment comment = getCommentEntity(boardId, taskId, commentId);

        // обновлять может только автор
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException("You cannot edit another user's comment");
        }

        if (payload.text() != null && !payload.text().equals(comment.getText())) {
            // task уже загружен через EntityGraph в findByIdAndTask_IdAndTask_Column_Board_Id
            eventPublisher.publishEvent(new TaskChangeEvent(
                    comment.getTask(), userId, ActionType.update, "COMMENT", comment.getText(), payload.text()
            ));
        }

        commentMapper.update(comment, payload);
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer boardId, Integer taskId, Integer commentId) {
        checkAccess(userId, boardId);

        Comment comment = getCommentEntity(boardId, taskId, commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException("You cannot delete another user's comment");
        }

        // task уже загружен через EntityGraph в findByIdAndTask_IdAndTask_Column_Board_Id
        eventPublisher.publishEvent(new TaskChangeEvent(
                comment.getTask(), userId, ActionType.delete, "COMMENT", comment.getText(), null
        ));

        commentRepository.delete(comment);
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

    private Comment getCommentEntity(Integer boardId, Integer taskId, Integer commentId) {
        return commentRepository.findByIdAndTask_IdAndTask_Column_Board_Id(commentId, taskId, boardId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + commentId));
    }
}
