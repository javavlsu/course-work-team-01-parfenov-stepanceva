package ru.ispi.kanban.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupTeamDTO;
import ru.ispi.kanban.payload.GroupTeamPayload;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.GroupTeamService;
import ru.ispi.kanban.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/groupteams/")
@RequiredArgsConstructor
public class GroupTeamController {

    private final GroupTeamService groupTeamService;

    private final AuthService authService;

//    @GetMapping("all")
//    public ResponseEntity<?> getAllGroupTeams()
//    {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(groupTeamService.getGroupTeams());
//    }

    @GetMapping()
    public ResponseEntity<List<GroupTeamDTO>> getUserGroupTeams(
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.getUserGroups(authService.getUserIdFromToken(accessToken)));
    }

    @GetMapping("{id}")
    public ResponseEntity<GroupTeamDTO> getGroupTeamById(
            @PathVariable Integer id,
            @CookieValue(value = "accessTokenKanban", required = false)
            String accessToken
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.get(id, authService.getUserIdFromToken(accessToken)));

    }

    @PostMapping()
    public ResponseEntity<GroupTeamDTO> createGroupTeam(@RequestBody GroupTeamPayload groupTeam, @CookieValue(value = "accessTokenKanban", required = false) String accessToken)
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupTeamService.create(groupTeam, authService.getUserIdFromToken(accessToken)));
    }

    @PutMapping("{id}")
    public ResponseEntity<GroupTeamDTO> updateGroupTeam(@PathVariable Integer id,
                                                                     @RequestBody GroupTeamPayload groupTeamPayload,
                                                                     @CookieValue(value = "accessTokenKanban", required = false) String accessToken) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupTeamService.update(id, authService.getUserIdFromToken(accessToken), groupTeamPayload));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteGroupTeam(@PathVariable Integer id,
                                                       @CookieValue(value = "accessTokenKanban", required = false) String accessToken) {


        groupTeamService.delete(id,authService.getUserIdFromToken(accessToken));

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
