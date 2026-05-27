package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.payloads.CreateEmailInvitationPayload;
import ru.ispi.kanban.payloads.CreateLinkInvitationPayload;
import ru.ispi.kanban.payloads.InvitationRespondPayload;
import ru.ispi.kanban.services.InvitationService;
import ru.ispi.kanban.utils.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/group/{groupId}/email")
    public ResponseEntity<InvitationDto> inviteByEmail(
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateEmailInvitationPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invitationService.inviteByEmail(groupId, SecurityUtils.requireCurrentUserId(), payload));
    }

    @PostMapping("/group/{groupId}/link")
    public ResponseEntity<InvitationDto> generateLink(
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateLinkInvitationPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invitationService.generateInviteLink(groupId, SecurityUtils.requireCurrentUserId(), payload));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<InvitationDto>> getGroupInvitations(@PathVariable Integer groupId) {
        return ResponseEntity.ok(invitationService.getGroupInvitations(groupId, SecurityUtils.requireCurrentUserId()));
    }

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Void> revokeInvitation(@PathVariable Integer invitationId) {
        invitationService.revokeInvitation(invitationId, SecurityUtils.requireCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvitationDto>> getMyInvitations() {
        return ResponseEntity.ok(invitationService.getMyInvitations(SecurityUtils.requireCurrentUserId()));
    }

    @PostMapping("/{invitationId}/respond")
    public ResponseEntity<InvitationDto> respond(
            @PathVariable Integer invitationId,
            @Valid @RequestBody InvitationRespondPayload payload
    ) {
        return ResponseEntity.ok(invitationService.respond(invitationId, SecurityUtils.requireCurrentUserId(), payload));
    }

    @PostMapping("/join/{token}")
    public ResponseEntity<InvitationDto> joinByLink(@PathVariable String token) {
        return ResponseEntity.ok(invitationService.joinByLink(token, SecurityUtils.requireCurrentUserId()));
    }
}
