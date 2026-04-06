package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.CommentDto;
import ru.ispi.kanban.payloads.CreateCommentPayload;
import ru.ispi.kanban.payloads.UpdateCommentPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/{boardId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getTaskComments(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByTask(user.getId(), boardId, taskId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @Valid @RequestBody CreateCommentPayload payload
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(user.getId(), boardId, taskId, payload));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer commentId,
            @Valid @RequestBody UpdateCommentPayload payload
    ) {
        return ResponseEntity.ok(commentService.update(user.getId(), boardId, taskId, commentId, payload));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer commentId
    ) {
        commentService.delete(user.getId(), boardId, taskId, commentId);
        return ResponseEntity.noContent().build();
    }
}