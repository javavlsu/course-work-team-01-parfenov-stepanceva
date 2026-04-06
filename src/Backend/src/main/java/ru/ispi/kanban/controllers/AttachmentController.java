package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.AttachmentDto;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.AttachmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/{boardId}/tasks/{taskId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping
    public ResponseEntity<List<AttachmentDto>> getTaskAttachments(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTask(user.getId(), boardId, taskId));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<AttachmentDto> upload(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.upload(user.getId(), boardId, taskId, file));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer attachmentId
    ) {
        attachmentService.delete(user.getId(), boardId, taskId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}