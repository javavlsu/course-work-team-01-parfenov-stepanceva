package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.TaskHistoryDto;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.TaskHistoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/{boardId}/tasks/{taskId}/history")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    @GetMapping
    public ResponseEntity<List<TaskHistoryDto>> getTaskHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(
                taskHistoryService.getHistoryByTask(user.getId(), boardId, taskId)
        );
    }
}