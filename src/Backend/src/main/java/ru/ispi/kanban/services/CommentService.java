package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsByTask(Integer userId, Integer boardId, Integer taskId);

    CommentDto create(Integer userId, Integer boardId, Integer taskId, CreateCommentPayload payload);

    CommentDto update(Integer userId, Integer boardId, Integer taskId, Integer commentId, UpdateCommentPayload payload);

    void delete(Integer userId, Integer boardId, Integer taskId, Integer commentId);
}
