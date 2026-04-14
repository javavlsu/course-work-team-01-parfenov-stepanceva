package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.payloads.CreateEmailInvitationPayload;
import ru.ispi.kanban.payloads.CreateLinkInvitationPayload;
import ru.ispi.kanban.payloads.InvitationRespondPayload;

import java.util.List;

public interface InvitationService {

    InvitationDto inviteByEmail(Integer groupId, Integer adminId, CreateEmailInvitationPayload payload);

    InvitationDto generateInviteLink(Integer groupId, Integer adminId, CreateLinkInvitationPayload payload);

    List<InvitationDto> getGroupInvitations(Integer groupId, Integer adminId);

    void revokeInvitation(Integer invitationId, Integer adminId);

    List<InvitationDto> getMyInvitations(Integer userId);

    InvitationDto respond(Integer invitationId, Integer userId, InvitationRespondPayload payload);

    InvitationDto joinByLink(String token, Integer userId);
}
