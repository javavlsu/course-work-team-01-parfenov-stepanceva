package ru.ispi.kanban.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.BoardUserService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/boards/{boardId}/users/")
@RequiredArgsConstructor
public class BoardUserController {

    private final BoardUserService boardUserService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                boardUserService.getUsersBoard(user.getId(), boardId)
        );
    }

    @PostMapping("{userId}")
    public ResponseEntity<Void> addUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ) {
        boardUserService.addUserToBoard(user.getId(), boardId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> removeUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ) {
        boardUserService.removeUserFromBoard(user.getId(), boardId, userId);
        return ResponseEntity.noContent().build();
    }

}
