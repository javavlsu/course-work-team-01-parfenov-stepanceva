package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ispi.kanban.dto.AttachmentDto;
import ru.ispi.kanban.services.AttachmentService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/tasks/{taskId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping
    public ResponseEntity<List<AttachmentDto>> getTaskAttachments(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTask(SecurityUtils.requireCurrentUserId(), boardId, taskId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> upload(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.upload(SecurityUtils.requireCurrentUserId(), boardId, taskId, file));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @PathVariable Integer attachmentId
    ) {
        attachmentService.delete(SecurityUtils.requireCurrentUserId(), boardId, taskId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}
