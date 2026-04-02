package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.TaskDto;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/boards/{boardId}/tasks/")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ){
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(taskService.GetTasks(user.getId(), boardId));
    }

}
