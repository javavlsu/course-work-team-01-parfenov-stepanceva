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
import ru.ispi.kanban.payloads.CreateBoardPayload;
import ru.ispi.kanban.payloads.UpdateBoardPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.BoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardDto>> getUserBoards(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                boardService.getUserBoards(user.getId())
        );
    }

    @GetMapping("group/{groupId}")
    public ResponseEntity<List<BoardDto>> getUserBoardsInGroup(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId
    ) {
        return ResponseEntity.ok(
                boardService.getUserBoardsInGroup(user.getId(), groupId)
        );
    }

    @GetMapping("{boardId}")
    public ResponseEntity<BoardDto> getBoard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                boardService.getUserBoard(user.getId(), boardId)
        );
    }

    @PostMapping
    public ResponseEntity<BoardDto> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateBoardPayload payload
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.create(payload, user.getId()));
    }

    @PutMapping("{boardId}")
    public ResponseEntity<BoardDto> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @Valid @RequestBody UpdateBoardPayload payload
    ) {
        return ResponseEntity.ok(
                boardService.update(payload, user.getId(), boardId)
        );
    }

    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        boardService.delete(user.getId(), boardId);
        return ResponseEntity.noContent().build();
    }
}
