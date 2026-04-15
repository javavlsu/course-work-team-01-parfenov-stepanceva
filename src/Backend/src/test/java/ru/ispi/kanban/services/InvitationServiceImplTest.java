package ru.ispi.kanban.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ispi.kanban.dto.InvitationDto;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.Invitation;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.enums.InvitationStatus;
import ru.ispi.kanban.enums.InvitationType;
import ru.ispi.kanban.exceptions.InvitationExpiredException;
import ru.ispi.kanban.exceptions.InvitationNotFoundException;
import ru.ispi.kanban.exceptions.MemberAlreadyExistsException;
import ru.ispi.kanban.exceptions.NoSuchUserByEmailException;
import ru.ispi.kanban.exceptions.ValidationException;
import ru.ispi.kanban.mappers.InvitationMapper;
import ru.ispi.kanban.payloads.CreateEmailInvitationPayload;
import ru.ispi.kanban.payloads.InvitationRespondPayload;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.InvitationRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.impl.InvitationServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceImplTest {

    @Mock InvitationRepository invitationRepository;

    @Mock GroupTeamRepository groupTeamRepository;

    @Mock UserRepository userRepository;

    @Mock GroupMemberService groupMemberService;

    @Mock InvitationMapper invitationMapper;

    @InjectMocks InvitationServiceImpl invitationService;

    @Test
    void inviteByEmail_success() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        User admin = new User();
        admin.setId(10);
        User invitedUser = new User();
        invitedUser.setId(5);

        doNothing().when(groupMemberService).checkAdmin(1, 10);
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(invitedUser));
        when(groupMemberService.isMember(1, 5)).thenReturn(false);
        when(invitationRepository.existsByGroupIdAndEmailAndStatus(1, "new@mail.com", InvitationStatus.PENDING)).thenReturn(false);
        when(groupTeamRepository.findById(1)).thenReturn(Optional.of(group));
        when(userRepository.findById(10)).thenReturn(Optional.of(admin));
        Invitation saved = new Invitation();
        when(invitationRepository.save(any())).thenReturn(saved);
        InvitationDto dto = new InvitationDto();
        when(invitationMapper.toDto(saved)).thenReturn(dto);

        InvitationDto result = invitationService.inviteByEmail(1, 10, new CreateEmailInvitationPayload("new@mail.com", 7));

        assertThat(result).isSameAs(dto);
    }

    @Test
    void inviteByEmail_userNotFound_throws() {
        doNothing().when(groupMemberService).checkAdmin(1, 10);
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.inviteByEmail(1, 10, new CreateEmailInvitationPayload("ghost@mail.com", 7)))
                .isInstanceOf(NoSuchUserByEmailException.class);
    }

    @Test
    void inviteByEmail_userAlreadyMember_throws() {
        User invitedUser = new User();
        invitedUser.setId(5);

        doNothing().when(groupMemberService).checkAdmin(1, 10);
        when(userRepository.findByEmail("member@mail.com")).thenReturn(Optional.of(invitedUser));
        when(groupMemberService.isMember(1, 5)).thenReturn(true);

        assertThatThrownBy(() -> invitationService.inviteByEmail(1, 10, new CreateEmailInvitationPayload("member@mail.com", 7)))
                .isInstanceOf(MemberAlreadyExistsException.class);
    }

    @Test
    void inviteByEmail_duplicatePendingInvitation_throws() {
        User invitedUser = new User();
        invitedUser.setId(5);

        doNothing().when(groupMemberService).checkAdmin(1, 10);
        when(userRepository.findByEmail("dup@mail.com")).thenReturn(Optional.of(invitedUser));
        when(groupMemberService.isMember(1, 5)).thenReturn(false);
        when(invitationRepository.existsByGroupIdAndEmailAndStatus(1, "dup@mail.com", InvitationStatus.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> invitationService.inviteByEmail(1, 10, new CreateEmailInvitationPayload("dup@mail.com", 7)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void joinByLink_success() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        Invitation invitation = new Invitation();
        invitation.setType(InvitationType.LINK);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setGroup(group);

        when(invitationRepository.findByInviteToken("token123")).thenReturn(Optional.of(invitation));
        when(groupMemberService.isMember(1, 5)).thenReturn(false);
        doNothing().when(groupMemberService).addMemberDirectly(1, 5, GroupRole.member);
        when(invitationMapper.toDto(invitation)).thenReturn(new InvitationDto());

        invitationService.joinByLink("token123", 5);

        verify(groupMemberService).addMemberDirectly(1, 5, GroupRole.member);
    }

    @Test
    void joinByLink_invalidToken_throws() {
        when(invitationRepository.findByInviteToken("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.joinByLink("bad", 5))
                .isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    void joinByLink_wrongType_throws() {
        Invitation invitation = new Invitation();
        invitation.setType(InvitationType.EMAIL);
        when(invitationRepository.findByInviteToken("token123")).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.joinByLink("token123", 5))
                .isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    void joinByLink_expired_throws() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        Invitation invitation = new Invitation();
        invitation.setType(InvitationType.LINK);
        invitation.setExpiresAt(LocalDateTime.now().minusDays(1));
        invitation.setGroup(group);

        when(invitationRepository.findByInviteToken("expired")).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.joinByLink("expired", 5))
                .isInstanceOf(InvitationExpiredException.class);
    }

    @Test
    void joinByLink_alreadyMember_throws() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        Invitation invitation = new Invitation();
        invitation.setType(InvitationType.LINK);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setGroup(group);

        when(invitationRepository.findByInviteToken("token123")).thenReturn(Optional.of(invitation));
        when(groupMemberService.isMember(1, 5)).thenReturn(true);

        assertThatThrownBy(() -> invitationService.joinByLink("token123", 5))
                .isInstanceOf(MemberAlreadyExistsException.class);
    }

    @Test
    void respond_accept_success() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        User user = new User();
        user.setId(5);
        user.setEmail("user@mail.com");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        Invitation invitation = new Invitation();
        invitation.setEmail("user@mail.com");
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setGroup(group);

        when(invitationRepository.findByIdAndEmail(1, "user@mail.com")).thenReturn(Optional.of(invitation));
        when(groupMemberService.isMember(1, 5)).thenReturn(false);
        when(invitationRepository.save(invitation)).thenReturn(invitation);
        when(invitationMapper.toDto(invitation)).thenReturn(new InvitationDto());

        invitationService.respond(1, 5, new InvitationRespondPayload(true));

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        verify(groupMemberService).addMemberDirectly(1, 5, GroupRole.member);
    }

    @Test
    void respond_decline_success() {
        GroupTeam group = new GroupTeam();
        group.setId(1);
        User user = new User();
        user.setId(5);
        user.setEmail("user@mail.com");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        Invitation invitation = new Invitation();
        invitation.setEmail("user@mail.com");
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setGroup(group);

        when(invitationRepository.findByIdAndEmail(1, "user@mail.com")).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(invitation);
        when(invitationMapper.toDto(invitation)).thenReturn(new InvitationDto());

        invitationService.respond(1, 5, new InvitationRespondPayload(false));

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.DECLINED);
        verify(groupMemberService, never()).addMemberDirectly(any(), any(), any());
    }

    @Test
    void respond_notPending_throws() {
        User user = new User();
        user.setId(5);
        user.setEmail("user@mail.com");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        Invitation invitation = new Invitation();
        invitation.setEmail("user@mail.com");
        invitation.setStatus(InvitationStatus.ACCEPTED);

        when(invitationRepository.findByIdAndEmail(1, "user@mail.com")).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.respond(1, 5, new InvitationRespondPayload(true)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void respond_expired_throws() {
        User user = new User();
        user.setId(5);
        user.setEmail("user@mail.com");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        Invitation invitation = new Invitation();
        invitation.setEmail("user@mail.com");
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(invitationRepository.findByIdAndEmail(1, "user@mail.com")).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.respond(1, 5, new InvitationRespondPayload(true)))
                .isInstanceOf(InvitationExpiredException.class);
    }
}
