package ru.ispi.kanban.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.services.BoardUserService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/users/")
@RequiredArgsConstructor
public class BoardUserController {

    private final BoardUserService boardUserService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@PathVariable Integer boardId) {
        return ResponseEntity.ok(boardUserService.getUsersBoard(SecurityUtils.requireCurrentUserId(), boardId));
    }

    @PostMapping("{userId}")
    public ResponseEntity<Void> addUser(@PathVariable Integer boardId, @PathVariable Integer userId) {
        boardUserService.addUserToBoard(SecurityUtils.requireCurrentUserId(), boardId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable Integer boardId, @PathVariable Integer userId) {
        boardUserService.removeUserFromBoard(SecurityUtils.requireCurrentUserId(), boardId, userId);
        return ResponseEntity.noContent().build();
    }
}
