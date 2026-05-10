package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.PageResponseDto;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.TaskPageQuery;
import ru.ispi.kanban.payloads.UpdateTaskPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/tasks/")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByBoard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByBoard(user.getId(), boardId)
        );
    }

    @GetMapping("{taskId}")
    public ResponseEntity<TaskDto> getTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        return ResponseEntity.ok(
                taskService.getTask(user.getId(), boardId, taskId)
        );
    }

    @GetMapping("column/{columnId}")
    public ResponseEntity<List<TaskDto>> getTasksByColumn(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer columnId
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByColumn(user.getId(), boardId, columnId)
        );
    }

    @GetMapping("my")
    public ResponseEntity<List<TaskDto>> getMyTasks(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByAssignee(user.getId(), boardId, user.getId())
        );
    }

    @GetMapping("page")
    public ResponseEntity<PageResponseDto<TaskDto>> getTasksPage(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @ModelAttribute TaskPageQuery query
    ) {
        return ResponseEntity.ok(taskService.getTasksPage(user.getId(), boardId, query));
    }

    @GetMapping("assignee/{assigneeId}")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer assigneeId
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByAssignee(user.getId(), boardId, assigneeId)
        );
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @Valid @RequestBody CreateTaskPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(user.getId(), boardId, payload));
    }

    @PutMapping("{taskId}")
    public ResponseEntity<TaskDto> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @Valid @RequestBody UpdateTaskPayload payload
    ) {
        return ResponseEntity.ok(
                taskService.update(user.getId(), boardId, taskId, payload)
        );
    }

    @DeleteMapping("{taskId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer taskId
    ) {
        taskService.delete(user.getId(), boardId, taskId);
        return ResponseEntity.noContent().build();
    }
}
