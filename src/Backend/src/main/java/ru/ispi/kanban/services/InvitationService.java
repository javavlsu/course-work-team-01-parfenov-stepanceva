package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.Invitation;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.enums.InvitationStatus;
import ru.ispi.kanban.enums.InvitationType;
import ru.ispi.kanban.exceptions.EntityNotFound;
import ru.ispi.kanban.exceptions.InvitationExpiredException;
import ru.ispi.kanban.exceptions.InvitationNotFoundException;
import ru.ispi.kanban.exceptions.MemberAlreadyExistsException;
import ru.ispi.kanban.exceptions.NoSuchUserByEmailException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.exceptions.ValidationException;
import ru.ispi.kanban.mappers.InvitationMapper;
import ru.ispi.kanban.payloads.CreateEmailInvitationPayload;
import ru.ispi.kanban.payloads.CreateLinkInvitationPayload;
import ru.ispi.kanban.payloads.InvitationRespondPayload;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.InvitationRepository;
import ru.ispi.kanban.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;

    private final GroupTeamRepository groupTeamRepository;

    private final UserRepository userRepository;

    private final GroupMemberService groupMemberService;

    private final InvitationMapper invitationMapper;

    @Transactional
    public InvitationDto inviteByEmail(Integer groupId, Integer adminId, CreateEmailInvitationPayload payload) {
        groupMemberService.checkAdmin(groupId, adminId);

        String email = payload.email();

        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchUserByEmailException("No user found with email: " + email));

        if (groupMemberService.isMember(groupId, invitedUser.getId())) {
            throw new MemberAlreadyExistsException("User is already a member of this group");
        }

        if (invitationRepository.existsByGroupIdAndEmailAndStatus(groupId, email, InvitationStatus.PENDING)) {
            throw new ValidationException("A pending invitation for this email already exists");
        }

        Invitation invitation = buildInvitation(
                getGroup(groupId), getUser(adminId),
                email, InvitationType.EMAIL, payload.expiresInDays()
        );

        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    @Transactional
    public InvitationDto generateInviteLink(Integer groupId, Integer adminId, CreateLinkInvitationPayload payload) {
        groupMemberService.checkAdmin(groupId, adminId);

        Invitation invitation = buildInvitation(
                getGroup(groupId), getUser(adminId),
                null, InvitationType.LINK, payload.expiresInDays()
        );

        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    @Transactional(readOnly = true)
    public List<InvitationDto> getGroupInvitations(Integer groupId, Integer adminId) {
        groupMemberService.checkAdmin(groupId, adminId);

        return invitationRepository.findByGroupId(groupId)
                .stream()
                .map(invitationMapper::toDto)
                .toList();
    }

    @Transactional
    public void revokeInvitation(Integer invitationId, Integer adminId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found: " + invitationId));

        groupMemberService.checkAdmin(invitation.getGroup().getId(), adminId);

        invitationRepository.delete(invitation);
    }


    @Transactional(readOnly = true)
    public List<InvitationDto> getMyInvitations(Integer userId) {
        String email = getUser(userId).getEmail();

        return invitationRepository
                .findByEmailAndStatusAndExpiresAtAfter(email, InvitationStatus.PENDING, LocalDateTime.now())
                .stream()
                .map(invitationMapper::toDto)
                .toList();
    }

    @Transactional
    public InvitationDto respond(Integer invitationId, Integer userId, InvitationRespondPayload payload) {
        String email = getUser(userId).getEmail();

        Invitation invitation = invitationRepository.findByIdAndEmail(invitationId, email)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found: " + invitationId));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new ValidationException("Invitation is no longer pending");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvitationExpiredException("Invitation has expired");
        }

        if (payload.accept()) {
            if (groupMemberService.isMember(invitation.getGroup().getId(), userId)) {
                throw new MemberAlreadyExistsException("You are already a member of this group");
            }
            groupMemberService.addMemberDirectly(invitation.getGroup().getId(), userId, GroupRole.member);
            invitation.setStatus(InvitationStatus.ACCEPTED);
        } else {
            invitation.setStatus(InvitationStatus.DECLINED);
        }

        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    @Transactional
    public InvitationDto joinByLink(String token, Integer userId) {
        Invitation invitation = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new InvitationNotFoundException("Invalid invite token"));

        if (invitation.getType() != InvitationType.LINK) {
            throw new InvitationNotFoundException("Invalid invite token");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvitationExpiredException("Invite link has expired");
        }

        if (groupMemberService.isMember(invitation.getGroup().getId(), userId)) {
            throw new MemberAlreadyExistsException("You are already a member of this group");
        }

        groupMemberService.addMemberDirectly(invitation.getGroup().getId(), userId, GroupRole.member);

        return invitationMapper.toDto(invitation);
    }

    private Invitation buildInvitation(GroupTeam group, User createdBy,
                                       String email, InvitationType type, int expiresInDays) {
        Invitation invitation = new Invitation();
        invitation.setGroup(group);
        invitation.setCreatedBy(createdBy);
        invitation.setEmail(email);
        invitation.setType(type);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setInviteToken(UUID.randomUUID().toString());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));
        return invitation;
    }

    private GroupTeam getGroup(Integer groupId) {
        return groupTeamRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFound("Group not found: " + groupId));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserByIdException("User not found: " + userId));
    }
}
