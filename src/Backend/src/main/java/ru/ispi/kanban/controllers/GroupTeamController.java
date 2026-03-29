package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.payload.GroupTeamPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.GroupTeamService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/groupteams/")
@RequiredArgsConstructor
public class GroupTeamController {

    private final GroupTeamService groupTeamService;

//    @GetMapping("all")
//    public ResponseEntity<?> getAllGroupTeams()
//    {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(groupTeamService.getGroupTeams());
//    }

    @GetMapping()
    public ResponseEntity<List<GroupTeamDto>> getUserGroupTeams(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.getUserGroups(user.getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<GroupTeamDto> getGroupTeamById(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.get(id, user.getId()));

    }

    @PostMapping()
    public ResponseEntity<GroupTeamDto> createGroupTeam(@Valid @RequestBody GroupTeamPayload groupTeam,
                                                        @AuthenticationPrincipal CustomUserDetails user)
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupTeamService.create(groupTeam, user.getId()));
    }

    @PutMapping("{id}")
    public ResponseEntity<GroupTeamDto> updateGroupTeam(@PathVariable Integer id,
                                                        @Valid @RequestBody GroupTeamPayload groupTeamPayload,
                                                        @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.update(id, user.getId(), groupTeamPayload));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteGroupTeam(@PathVariable Integer id,
                                                @AuthenticationPrincipal CustomUserDetails user) {


        groupTeamService.delete(id,user.getId());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
