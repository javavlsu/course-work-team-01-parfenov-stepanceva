package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;
import ru.ispi.kanban.services.CommentService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getTaskComments(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByTask(SecurityUtils.requireCurrentUserId(), boardId, taskId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @Valid @RequestBody CreateCommentPayload payload
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(SecurityUtils.requireCurrentUserId(), boardId, taskId, payload));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer commentId,
            @Valid @RequestBody UpdateCommentPayload payload
    ) {
        return ResponseEntity.ok(commentService.update(SecurityUtils.requireCurrentUserId(), boardId, taskId, commentId, payload));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer commentId
    ) {
        commentService.delete(SecurityUtils.requireCurrentUserId(), boardId, taskId, commentId);
        return ResponseEntity.noContent().build();
    }
}
