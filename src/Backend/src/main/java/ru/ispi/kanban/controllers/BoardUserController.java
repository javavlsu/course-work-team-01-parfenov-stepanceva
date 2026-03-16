package ru.ispi.kanban.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.BoardUserService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/board-users/")
@RequiredArgsConstructor
public class BoardUserController {

    private final BoardUserService boardUserService;

    public final AuthService authService;

    @GetMapping("{groupId}/{boardId}")
    public ResponseEntity<List<UserDTO>> getUsers(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId, @PathVariable Integer boardId
    ){

        Integer adminId = authService.getUserIdFromToken(accessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardUserService.getUsersBoard(adminId, groupId, boardId));

    }

    @PostMapping("/{groupId}/{boardId}/{userId}")
    public ResponseEntity<Void> addUser(
            @CookieValue(value = "accessTokenKanban") String token,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ){
        Integer adminId = authService.getUserIdFromToken(token);

        boardUserService.addUserToBoard(adminId, groupId, boardId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{groupId}/{boardId}/{userId}")
    public ResponseEntity<Void> removeUser(
            @CookieValue(value = "accessTokenKanban") String token,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId,
            @PathVariable Integer userId
    ){
        Integer adminId = authService.getUserIdFromToken(token);

        boardUserService.removeUserFromBoard(adminId, groupId, boardId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
