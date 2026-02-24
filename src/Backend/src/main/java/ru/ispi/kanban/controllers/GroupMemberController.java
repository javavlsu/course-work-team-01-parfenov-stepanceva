package ru.ispi.kanban.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.ApiResponse;
import ru.ispi.kanban.dto.GroupMemberDTO;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.payload.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payload.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.services.AuthService;
import ru.ispi.kanban.services.GroupMemberService;
import ru.ispi.kanban.util.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/group-member/")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService memberService;

    private final AuthService authService;

    @GetMapping("{groupId}")
    public ResponseEntity<ApiResponse<List<GroupMemberDTO>>> members(
            @PathVariable Integer groupId,
            @CookieValue(value = "accessTokenKanban", required = false)
                    String accessToken
    ) {

        Integer userId = authService.getUserIdFromToken(accessToken);

        memberService.checkMember(groupId, userId);

        return ResponseEntity.ok(
                ApiResponses.ok("Group members",
                        memberService.getGroupMembers(groupId))
        );
    }

    @PostMapping("{groupId}")
    public ResponseEntity<ApiResponse<GroupMemberDTO>> add(
            @PathVariable Integer groupId,
            @RequestBody AddMemberToGroupTeamPayload payload,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        return ResponseEntity.ok(
                ApiResponses.ok(
                        "Member added",
                        memberService.addMember(groupId,adminId, payload)
                )
        );
    }

    @PutMapping("{groupId}/{userId}")
    public ResponseEntity<ApiResponse<GroupMemberDTO>> updateRole(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @RequestBody UpdateMemberRoleInGroupTeamPayload payload,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        return ResponseEntity.ok(
                ApiResponses.ok(
                        "Role updated",
                        memberService.updateRole(adminId, groupId, userId, payload)
                )
        );
    }

    @DeleteMapping("{groupId}/{userId}")
    public ResponseEntity<ApiResponse<?>> remove(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @CookieValue(value = "accessTokenKanban", required = false) String accessToken
    ) {

        Integer adminId = authService.getUserIdFromToken(accessToken);

        memberService.deleteMember(adminId, groupId, userId);

        return ResponseEntity.ok(
                ApiResponses.ok("Member removed", null)
        );
    }

}
