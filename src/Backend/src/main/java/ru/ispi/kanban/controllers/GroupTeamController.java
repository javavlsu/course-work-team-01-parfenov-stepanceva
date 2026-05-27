package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.payloads.GroupTeamPayload;
import ru.ispi.kanban.services.GroupTeamService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/groupteams/")
@RequiredArgsConstructor
public class GroupTeamController {

    private final GroupTeamService groupTeamService;

    @GetMapping()
    public ResponseEntity<List<GroupTeamDto>> getUserGroupTeams() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.getUserGroups(SecurityUtils.requireCurrentUserId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<GroupTeamDto> getGroupTeamById(@PathVariable Integer id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.get(id, SecurityUtils.requireCurrentUserId()));
    }

    @PostMapping()
    public ResponseEntity<GroupTeamDto> createGroupTeam(@Valid @RequestBody GroupTeamPayload groupTeam) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupTeamService.create(groupTeam, SecurityUtils.requireCurrentUserId()));
    }

    @PutMapping("{id}")
    public ResponseEntity<GroupTeamDto> updateGroupTeam(
            @PathVariable Integer id,
            @Valid @RequestBody GroupTeamPayload groupTeamPayload
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.update(id, SecurityUtils.requireCurrentUserId(), groupTeamPayload));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteGroupTeam(@PathVariable Integer id) {
        groupTeamService.delete(id, SecurityUtils.requireCurrentUserId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
