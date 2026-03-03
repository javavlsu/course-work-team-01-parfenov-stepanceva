package ru.ispi.kanban.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupMemberDTO;
import ru.ispi.kanban.payload.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payload.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.GroupMemberService;
import ru.ispi.kanban.services.GroupTeamService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/group-members/")
@RequiredArgsConstructor
public class  GroupMemberController {

    private final GroupMemberService memberService;

    private final GroupTeamService groupTeamService;

    private final AuthService authService;

    @GetMapping("{groupId}")
    public ResponseEntity<List<GroupMemberDTO>> members(
            @PathVariable Integer groupId,
            @CookieValue(value = "accessTokenKanban", required = false)
                    String accessToken
    ) {

        Integer userId = authService.getUserIdFromToken(accessToken);

        memberService.checkMember(groupId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.getGroupMembers(groupId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<GroupMemberDTO> add(
            @PathVariable Integer groupId,
            @RequestBody AddMemberToGroupTeamPayload payload,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.addMember(groupId,adminId, payload));

    }

    @PutMapping("{groupId}/{userId}")
    public ResponseEntity<GroupMemberDTO> updateRole(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @RequestBody UpdateMemberRoleInGroupTeamPayload payload,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.updateRole(adminId, groupId, userId, payload));
    }

    @DeleteMapping("{groupId}/{userId}")
    public ResponseEntity<?> remove(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        memberService.deleteMember(adminId, groupId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
