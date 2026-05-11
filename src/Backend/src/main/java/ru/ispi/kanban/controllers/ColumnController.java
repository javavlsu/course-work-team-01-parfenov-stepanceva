package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.ColumnDto;
import ru.ispi.kanban.payloads.CreateColumnPayload;
import ru.ispi.kanban.payloads.UpdateColumnPayload;
import ru.ispi.kanban.services.ColumnService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/columns/")
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping
    public ResponseEntity<List<ColumnDto>> getColumns(@PathVariable Integer boardId) {
        return ResponseEntity.ok(columnService.getColumns(SecurityUtils.requireCurrentUserId(), boardId));
    }

    @PostMapping
    public ResponseEntity<ColumnDto> create(
            @PathVariable Integer boardId,
            @Valid @RequestBody CreateColumnPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(columnService.create(SecurityUtils.requireCurrentUserId(), boardId, payload));
    }

    @PutMapping("{columnId}")
    public ResponseEntity<ColumnDto> update(
            @PathVariable Integer boardId,
            @PathVariable Integer columnId,
            @Valid @RequestBody UpdateColumnPayload payload
    ) {
        return ResponseEntity.ok(columnService.update(SecurityUtils.requireCurrentUserId(), boardId, columnId, payload));
    }

    @DeleteMapping("{columnId}")
    public ResponseEntity<Void> delete(@PathVariable Integer boardId, @PathVariable Integer columnId) {
        columnService.delete(SecurityUtils.requireCurrentUserId(), boardId, columnId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{columnId}/move")
    public ResponseEntity<Void> move(
            @PathVariable Integer boardId,
            @PathVariable Integer columnId,
            @RequestParam Long newPosition
    ) {
        columnService.move(SecurityUtils.requireCurrentUserId(), boardId, columnId, newPosition);
        return ResponseEntity.ok().build();
    }
}
