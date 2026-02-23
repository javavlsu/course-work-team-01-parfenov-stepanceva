package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.ApiResponse;
import ru.ispi.kanban.dto.GroupTeamDTO;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.payload.GroupTeamPayload;
import ru.ispi.kanban.services.GroupTeamService;
import ru.ispi.kanban.util.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/groupteam/")
@RequiredArgsConstructor
public class GroupTeamController {

    private final GroupTeamService groupTeamService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<GroupTeam>>> getGroupTeam()
    {
        return ResponseEntity.
                status(200)
                .body(ApiResponses.ok("List of group teams get", groupTeamService.getGroupTeams()));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<GroupTeam>> getGroupTeamById(@PathVariable Integer id,
                                                                   @CookieValue(value = "accessTokenKanban", required = false) String accessToken) {
        GroupTeam groupTeam = groupTeamService.get(id);
        return ResponseEntity.
                status(200)
                .body(ApiResponses.ok("Group team found", groupTeam));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createGroupTeam(@RequestBody GroupTeamPayload groupTeam, @CookieValue(value = "accessTokenKanban", required = false) String accessToken)
    {
        GroupTeamDTO groupTeamDTO = groupTeamService.create(groupTeam);

        return ResponseEntity.
                status(201)
                .body(ApiResponses.ok("Group team created success", groupTeamDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<GroupTeamDTO>> updateGroupTeam(@PathVariable Integer id,
                                                                     @RequestBody GroupTeamPayload groupTeamPayload,
                                                                     @CookieValue(value = "accessTokenKanban", required = false) String accessToken) {
        GroupTeamDTO updatedGroupTeam = groupTeamService.update(id, groupTeamPayload);
        return ResponseEntity.
                status(200)
                .body(ApiResponses.ok("Group team updated success", updatedGroupTeam));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteGroupTeam(@PathVariable Integer id,
                                                       @CookieValue(value = "accessTokenKanban", required = false) String accessToken) {
        groupTeamService.delete(id);
        return ResponseEntity.
                status(200)
                .body(ApiResponses.ok("Group team deleted success", null));
    }

}
