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
@RequestMapping("/api/kanban/board-users/")
@RequiredArgsConstructor
public class BoardUserController {

    private final BoardUserService boardUserService;

    @GetMapping("{groupId}/{boardId}")
    public ResponseEntity<List<UserDto>> getUsers(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable Integer groupId, @PathVariable Integer boardId
    ){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardUserService.getUsersBoard(user.getId(), groupId, boardId));

    }

    @PostMapping("/{groupId}/{boardId}/{userId}")
    public ResponseEntity<Void> addUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ){


        boardUserService.addUserToBoard(user.getId(), groupId, boardId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{groupId}/{boardId}/{userId}")
    public ResponseEntity<Void> removeUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ){

        boardUserService.removeUserFromBoard(user.getId(), groupId, boardId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
