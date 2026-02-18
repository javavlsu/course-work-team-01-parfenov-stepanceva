package ru.ispi.kanban.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/kanban/")
public class TestController {
    
    @GetMapping("hello")
    public String sayHello(){
        return "Hello kanban!!";
    }
}
