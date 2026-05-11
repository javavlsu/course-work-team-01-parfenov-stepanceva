package ru.ispi.kanban.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.payloads.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payloads.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.services.GroupMemberService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/group-members/")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService memberService;

    @GetMapping("{groupId}")
    public ResponseEntity<List<GroupMemberDto>> members(@PathVariable Integer groupId) {
        memberService.checkMember(groupId, SecurityUtils.requireCurrentUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.getGroupMembers(groupId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<GroupMemberDto> add(
            @PathVariable Integer groupId,
            @Valid @RequestBody AddMemberToGroupTeamPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.addMember(groupId, SecurityUtils.requireCurrentUserId(), payload));
    }

    @PutMapping("{groupId}/users/{userId}")
    public ResponseEntity<GroupMemberDto> updateRole(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateMemberRoleInGroupTeamPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.updateRole(SecurityUtils.requireCurrentUserId(), groupId, userId, payload));
    }

    @DeleteMapping("{groupId}/users/{userId}")
    public ResponseEntity<Void> remove(
            @PathVariable Integer groupId,
            @PathVariable Integer userId
    ) {
        memberService.deleteMember(SecurityUtils.requireCurrentUserId(), groupId, userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
