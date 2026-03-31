package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.ColumnDto;
import ru.ispi.kanban.payloads.CreateColumnPayload;
import ru.ispi.kanban.payloads.UpdateColumnPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.ColumnService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards/{boardId}/columns/")
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping
    public ResponseEntity<List<ColumnDto>> getColumns(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId
    ) {
        return ResponseEntity.ok(
                columnService.getColumns(user.getId(), boardId)
        );
    }

    @PostMapping
    public ResponseEntity<ColumnDto> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @Valid @RequestBody CreateColumnPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(columnService.create(user.getId(), boardId, payload));
    }

    @PutMapping("{columnId}")
    public ResponseEntity<ColumnDto> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer columnId,
            @Valid @RequestBody UpdateColumnPayload payload
    ) {
        return ResponseEntity.ok(
                columnService.update(user.getId(), boardId, columnId, payload)
        );
    }

    @DeleteMapping("{columnId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer columnId
    ) {
        columnService.delete(user.getId(), boardId, columnId);

        return ResponseEntity.noContent().build();
    }

    // перемещение колонки
    @PutMapping("{columnId}/move")
    public ResponseEntity<Void> move(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer boardId,
            @PathVariable Integer columnId,
            @RequestParam Long newPosition
    ) {
        columnService.move(user.getId(), boardId, columnId, newPosition);
        return ResponseEntity.ok().build();
    }
}