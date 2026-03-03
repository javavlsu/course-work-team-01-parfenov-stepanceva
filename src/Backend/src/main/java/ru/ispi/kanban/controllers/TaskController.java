package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kanban/tasks")
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
