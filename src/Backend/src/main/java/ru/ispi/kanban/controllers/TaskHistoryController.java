package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.TaskHistoryDto;
import ru.ispi.kanban.services.TaskHistoryService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/tasks/{taskId}/history")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    @GetMapping
    public ResponseEntity<List<TaskHistoryDto>> getTaskHistory(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(
                taskHistoryService.getHistoryByTask(SecurityUtils.requireCurrentUserId(), boardId, taskId)
        );
    }
}
