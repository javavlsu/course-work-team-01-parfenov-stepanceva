package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.payload.CreateBoardPayload;
import ru.ispi.kanban.payload.UpdateBoardPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.BoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/")
public class BoardController {

    private final BoardService boardService;

    //GetAllUserBoardsInTeam - т.к. доски доступные именно этому пользователю в группе
    @GetMapping("{groupId}")
    public ResponseEntity<List<BoardDto>> GetAllUserBoardsInTeam(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.getUserBoards(user.getId(), groupId));
    }

    @GetMapping("{groupId}/{boardId}")
    public ResponseEntity<BoardDto> GetUserBoardInTeam(
            @AuthenticationPrincipal CustomUserDetails user,
             @PathVariable Integer groupId, @PathVariable Integer boardId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.getUserBoard(user.getId(), groupId, boardId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<BoardDto> CreateBoard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateBoardPayload payload
            ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(boardService.create(payload, user.getId(), groupId));
    }

    @PutMapping("{groupId}/{boardId}")
    public ResponseEntity<BoardDto> UpdateBoard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId,
            @Valid @RequestBody UpdateBoardPayload payload){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(boardService.update(payload, user.getId(), groupId, boardId));
    }


    @DeleteMapping("{groupId}/{boardId}")
    public ResponseEntity<Void> DeleteBoard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @PathVariable Integer boardId
    )
    {
        boardService.delete(user.getId(), groupId, boardId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
