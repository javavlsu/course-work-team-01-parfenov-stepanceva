package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.BoardDTO;
import ru.ispi.kanban.payload.CreateBoardPayload;
import ru.ispi.kanban.payload.UpdateBoardPayload;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.BoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/")
public class BoardController {

    private final AuthService authService;

    private final BoardService boardService;


    //GetAllUserBoardsInTeam - т.к. доски доступные именно этому пользователю в группе
    @GetMapping("{groupId}")
    public ResponseEntity<List<BoardDTO>> GetAllUserBoardsInTeam(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.getUserBoards(authService.getUserIdFromToken(accessToken), groupId));
    }

    @GetMapping("{groupId}/{boardId}")
    public ResponseEntity<BoardDTO> GetUserBoardInTeam(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId, @PathVariable Integer boardId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.getUserBoard(authService.getUserIdFromToken(accessToken), groupId, boardId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<BoardDTO> CreateBoard(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId,@Valid @RequestBody CreateBoardPayload payload
            ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardService.create(payload, authService.getUserIdFromToken(accessToken), groupId));
    }

    @PutMapping("{groupId}/{boardId}")
    public ResponseEntity<BoardDTO> UpdateBoard(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId, @PathVariable Integer boardId,@Valid @RequestBody UpdateBoardPayload payload){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.update(payload, authService.getUserIdFromToken(accessToken), groupId, boardId));
    }


    @DeleteMapping("{groupId}/{boardId}")
    public ResponseEntity<Void> DeleteBoard(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken, @PathVariable Integer groupId, @PathVariable Integer boardId
    )
    {
        boardService.delete(authService.getUserIdFromToken(accessToken), groupId, boardId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
