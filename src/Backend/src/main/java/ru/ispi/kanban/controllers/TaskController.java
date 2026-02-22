package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.ApiResponse;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.entity.Task;
import ru.ispi.kanban.util.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/task")
@RequiredArgsConstructor
public class TaskController {

//    private final TaskService taskService;
//
//    @GetMapping
//    public ResponseEntity<List<Task>> getTasks(){
//        return ResponseEntity.
//                status(200)
//                .body(ApiResponses.ok("List of tasks getted", taskService.getTasks()));
//    }

}
