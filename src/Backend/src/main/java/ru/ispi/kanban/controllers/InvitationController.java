package ru.ispi.kanban.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.payloads.CreateEmailInvitationPayload;
import ru.ispi.kanban.payloads.CreateLinkInvitationPayload;
import ru.ispi.kanban.payloads.InvitationRespondPayload;
import ru.ispi.kanban.security.CustomUserDetails;
import ru.ispi.kanban.services.InvitationService;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/group/{groupId}/email")
    public ResponseEntity<InvitationDto> inviteByEmail(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateEmailInvitationPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invitationService.inviteByEmail(groupId, user.getId(), payload));
    }

    // Сгенерировать ссылку-приглашение (многоразовая, действует до expiresInDays).
    @PostMapping("/group/{groupId}/link")
    public ResponseEntity<InvitationDto> generateLink(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateLinkInvitationPayload payload
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invitationService.generateInviteLink(groupId, user.getId(), payload));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<InvitationDto>> getGroupInvitations(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer groupId
    ) {
        return ResponseEntity.ok(invitationService.getGroupInvitations(groupId, user.getId()));
    }

    // Отозвать приглашение
    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Void> revokeInvitation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer invitationId
    ) {
        invitationService.revokeInvitation(invitationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<InvitationDto>> getMyInvitations(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(invitationService.getMyInvitations(user.getId()));
    }

    // Body: { "accept": true } или { "accept": false }
    @PostMapping("/{invitationId}/respond")
    public ResponseEntity<InvitationDto> respond(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Integer invitationId,
            @Valid @RequestBody InvitationRespondPayload payload
    ) {
        return ResponseEntity.ok(invitationService.respond(invitationId, user.getId(), payload));
    }

    @PostMapping("/join/{token}")
    public ResponseEntity<InvitationDto> joinByLink(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String token
    ) {
        return ResponseEntity.ok(invitationService.joinByLink(token, user.getId()));
    }
}
