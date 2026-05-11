package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.ispi.kanban.services.BoardService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardDto>> getUserBoards() {
        return ResponseEntity.ok(boardService.getUserBoards(SecurityUtils.requireCurrentUserId()));
    }

    @GetMapping("group/{groupId}")
    public ResponseEntity<List<BoardDto>> getUserBoardsInGroup(@PathVariable Integer groupId) {
        return ResponseEntity.ok(boardService.getUserBoardsInGroup(SecurityUtils.requireCurrentUserId(), groupId));
    }

    @GetMapping("{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Integer boardId) {
        return ResponseEntity.ok(boardService.getUserBoard(SecurityUtils.requireCurrentUserId(), boardId))
;
    }

    @PostMapping
    public ResponseEntity<BoardDto> create(@Valid @RequestBody CreateBoardPayload payload) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.create(payload, SecurityUtils.requireCurrentUserId()));
    }

    @PutMapping("{boardId}")
    public ResponseEntity<BoardDto> update(
            @PathVariable Integer boardId,
            @Valid @RequestBody UpdateBoardPayload payload
    ) {
        return ResponseEntity.ok(boardService.update(payload, SecurityUtils.requireCurrentUserId(), boardId));
    }

    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> delete(@PathVariable Integer boardId) {
        boardService.delete(SecurityUtils.requireCurrentUserId(), boardId);
        return ResponseEntity.noContent().build();
    }
}
