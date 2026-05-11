package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.PageResponseDto;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.payloads.CreateTaskPayload;
import ru.ispi.kanban.payloads.TaskPageQuery;
import ru.ispi.kanban.payloads.UpdateTaskPayload;
import ru.ispi.kanban.services.TaskService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/tasks/")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByBoard(@PathVariable Integer boardId) {
        return ResponseEntity.ok(taskService.getTasksByBoard(SecurityUtils.requireCurrentUserId(), boardId));
    }

    @GetMapping("{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Integer boardId, @PathVariable Integer taskId) {
        return ResponseEntity.ok(taskService.getTask(SecurityUtils.requireCurrentUserId(), boardId, taskId));
    }

    @GetMapping("column/{columnId}")
    public ResponseEntity<List<TaskDto>> getTasksByColumn(@PathVariable Integer boardId, @PathVariable Integer columnId) {
        return ResponseEntity.ok(taskService.getTasksByColumn(SecurityUtils.requireCurrentUserId(), boardId, columnId));
    }

    @GetMapping("my")
    public ResponseEntity<List<TaskDto>> getMyTasks(@PathVariable Integer boardId) {
        Integer userId = SecurityUtils.requireCurrentUserId();
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId, boardId, userId));
    }

    @GetMapping("page")
    public ResponseEntity<PageResponseDto<TaskDto>> getTasksPage(
            @PathVariable Integer boardId,
            @ModelAttribute TaskPageQuery query
    ) {
        return ResponseEntity.ok(taskService.getTasksPage(SecurityUtils.requireCurrentUserId(), boardId, query));
    }

    @GetMapping("assignee/{assigneeId}")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(
            @PathVariable Integer boardId,
            @PathVariable Integer assigneeId
    ) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(SecurityUtils.requireCurrentUserId(), boardId, assigneeId));
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(
            @PathVariable Integer boardId,
            @Valid @RequestBody CreateTaskPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(SecurityUtils.requireCurrentUserId(), boardId, payload));
    }

    @PutMapping("{taskId}")
    public ResponseEntity<TaskDto> update(
            @PathVariable Integer boardId,
            @PathVariable Integer taskId,
            @Valid @RequestBody UpdateTaskPayload payload
    ) {
        return ResponseEntity.ok(taskService.update(SecurityUtils.requireCurrentUserId(), boardId, taskId, payload));
    }

    @DeleteMapping("{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Integer boardId, @PathVariable Integer taskId) {
        taskService.delete(SecurityUtils.requireCurrentUserId(), boardId, taskId);
        return ResponseEntity.noContent().build();
    }
}
