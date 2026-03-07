package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.BoardDTO;
import ru.ispi.kanban.payload.BoardPayload;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.BoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/")
public class BoardController {

    private final AuthService authService;

    private final BoardService boardService;

    @GetMapping("{groupId}")
    public ResponseEntity<List<BoardDTO>> GetAllUserBoards(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.getUserBoards(authService.getUserIdFromToken(accessToken), groupId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<BoardDTO> CreateBoard(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId, @RequestBody BoardPayload payload
            ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardService.create(payload, authService.getUserIdFromToken(accessToken), groupId));
    }

}
