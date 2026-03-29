package ru.ispi.kanban.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.payload.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payload.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.GroupMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/kanban/group-members/")
@RequiredArgsConstructor
public class  GroupMemberController {

    private final GroupMemberService memberService;

    @GetMapping("{groupId}")
    public ResponseEntity<List<GroupMemberDto>> members(
            @PathVariable Integer groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        memberService.checkMember(groupId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.getGroupMembers(groupId));
    }

    @PostMapping("{groupId}")
    public ResponseEntity<GroupMemberDto> add(
            @PathVariable Integer groupId,
            @Valid @RequestBody AddMemberToGroupTeamPayload payload,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.addMember(groupId,user.getId(), payload));

    }

    @PutMapping("{groupId}/{userId}")
    public ResponseEntity<GroupMemberDto> updateRole(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateMemberRoleInGroupTeamPayload payload,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.updateRole(user.getId(), groupId, userId, payload));
    }

    @DeleteMapping("{groupId}/{userId}")
    public ResponseEntity<Void> remove(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        memberService.deleteMember(user.getId(), groupId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
