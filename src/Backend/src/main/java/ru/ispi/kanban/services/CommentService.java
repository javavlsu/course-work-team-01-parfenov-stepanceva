package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.entities.Comment;
import ru.ispi.kanban.entities.Task;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.mappers.CommentMapper;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.CommentRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final BoardUserRepository boardUserRepository;

    private final CommentMapper commentMapper;

    public List<CommentDto> getCommentsByTask(Integer userId, Integer boardId, Integer taskId) {
        checkAccess(userId, boardId);
        return commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto create(Integer userId, Integer boardId, Integer taskId, CreateCommentPayload payload) {
        checkAccess(userId, boardId);

        Comment comment = commentMapper.toEntity(payload);
        comment.setTask(getTask(boardId, taskId));
        comment.setUser(getUser(userId));

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(Integer userId, Integer boardId, Integer taskId, Integer commentId, UpdateCommentPayload payload) {
        checkAccess(userId, boardId);

        Comment comment = getCommentEntity(boardId, taskId, commentId);

        // обновлять может только автор
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Вы не можете редактировать чужой комментарий");
        }

        commentMapper.update(comment, payload);

        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Integer userId, Integer boardId, Integer taskId, Integer commentId) {
        checkAccess(userId, boardId);

        Comment comment = getCommentEntity(boardId, taskId, commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Вы не можете удалить чужой комментарий");
        }

        commentRepository.delete(comment);
    }

    private void checkAccess(Integer userId, Integer boardId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new RuntimeException("Нет доступа к доске");
        }
    }

    private Task getTask(Integer boardId, Integer taskId) {
        return taskRepository.findByIdAndColumn_Board_Id(taskId, boardId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    private Comment getCommentEntity(Integer boardId, Integer taskId, Integer commentId) {
        return commentRepository.findByIdAndTask_IdAndTask_Column_Board_Id(commentId, taskId, boardId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
    }
}
