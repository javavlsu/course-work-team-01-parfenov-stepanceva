package ru.ispi.kanban.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.entities.GroupMember;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.exceptions.MemberAlreadyExistsException;
import ru.ispi.kanban.exceptions.NotAdminException;
import ru.ispi.kanban.exceptions.NotMemberException;
import ru.ispi.kanban.listeners.AdminAssignedEvent;
import ru.ispi.kanban.mappers.GroupMemberMapper;
import ru.ispi.kanban.payloads.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payloads.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.repositories.GroupMemberRepository;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.impl.GroupMemberServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMemberServiceImplTest {

    @Mock GroupMemberRepository memberRepository;

    @Mock GroupTeamRepository groupRepository;

    @Mock UserRepository userRepository;

    @Mock GroupMemberMapper groupMemberMapper;

    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks GroupMemberServiceImpl groupMemberService;

    @Test
    void checkAdmin_success() {
        when(memberRepository.findByGroupIdAndUserId(1, 1)).thenReturn(Optional.of(memberWithRole(1, 1, GroupRole.admin)));
        groupMemberService.checkAdmin(1, 1);
    }

    @Test
    void checkAdmin_notMember_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupMemberService.checkAdmin(1, 99))
                .isInstanceOf(NotMemberException.class);
    }

    @Test
    void checkAdmin_notAdmin_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 1)).thenReturn(Optional.of(memberWithRole(1, 1, GroupRole.member)));

        assertThatThrownBy(() -> groupMemberService.checkAdmin(1, 1))
                .isInstanceOf(NotAdminException.class);
    }

    @Test
    void checkMember_success() {
        when(memberRepository.findByGroupIdAndUserId(1, 2)).thenReturn(Optional.of(new GroupMember()));
        groupMemberService.checkMember(1, 2);
    }

    @Test
    void checkMember_notMember_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupMemberService.checkMember(1, 2))
                .isInstanceOf(NotMemberException.class);
    }

    @Test
    void addMember_memberRole_success_noEvent() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));
        when(memberRepository.existsByGroupIdAndUserId(1, 5)).thenReturn(false);
        GroupTeam group = new GroupTeam();
        group.setId(1);
        when(groupRepository.getReferenceById(1)).thenReturn(group);
        User user = new User();
        user.setId(5);
        when(userRepository.findById(5)).thenReturn(Optional.of(user));
        GroupMember saved = new GroupMember();
        when(memberRepository.save(any())).thenReturn(saved);
        when(groupMemberMapper.toDto(saved)).thenReturn(new GroupMemberDto());

        GroupMemberDto result = groupMemberService.addMember(1, 10, new AddMemberToGroupTeamPayload(5, GroupRole.member));

        assertThat(result).isNotNull();
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void addMember_adminRole_publishesEvent() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));
        when(memberRepository.existsByGroupIdAndUserId(1, 6)).thenReturn(false);
        GroupTeam group = new GroupTeam();
        group.setId(1);
        when(groupRepository.getReferenceById(1)).thenReturn(group);
        User user = new User();
        user.setId(6);
        when(userRepository.findById(6)).thenReturn(Optional.of(user));
        GroupMember saved = new GroupMember();
        when(memberRepository.save(any())).thenReturn(saved);
        when(groupMemberMapper.toDto(saved)).thenReturn(new GroupMemberDto());

        groupMemberService.addMember(1, 10, new AddMemberToGroupTeamPayload(6, GroupRole.admin));

        ArgumentCaptor<AdminAssignedEvent> captor = ArgumentCaptor.forClass(AdminAssignedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().groupId()).isEqualTo(1);
        assertThat(captor.getValue().user()).isSameAs(user);
    }

    @Test
    void addMember_alreadyMember_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));
        when(memberRepository.existsByGroupIdAndUserId(1, 5)).thenReturn(true);

        assertThatThrownBy(() -> groupMemberService.addMember(1, 10, new AddMemberToGroupTeamPayload(5, GroupRole.member)))
                .isInstanceOf(MemberAlreadyExistsException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    void addMember_callerIsNotAdmin_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 7)).thenReturn(Optional.of(memberWithRole(1, 7, GroupRole.member)));

        assertThatThrownBy(() -> groupMemberService.addMember(1, 7, new AddMemberToGroupTeamPayload(5, GroupRole.member)))
                .isInstanceOf(NotAdminException.class);
    }

    @Test
    void updateRole_memberToAdmin_publishesEvent() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));
        User targetUser = new User();
        targetUser.setId(3);
        GroupMember targetMember = memberWithRole(1, 3, GroupRole.member);
        targetMember.setUser(targetUser);
        when(memberRepository.findByGroupIdAndUserId(1, 3)).thenReturn(Optional.of(targetMember));
        when(memberRepository.save(targetMember)).thenReturn(targetMember);
        when(groupMemberMapper.toDto(targetMember)).thenReturn(new GroupMemberDto());

        groupMemberService.updateRole(10, 1, 3, new UpdateMemberRoleInGroupTeamPayload(GroupRole.admin));

        assertThat(targetMember.getRole()).isEqualTo(GroupRole.admin);
        verify(eventPublisher).publishEvent(any(AdminAssignedEvent.class));
    }

    @Test
    void updateRole_adminToAdmin_noEvent() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));
        GroupMember targetMember = memberWithRole(1, 3, GroupRole.admin);
        targetMember.setUser(new User());
        when(memberRepository.findByGroupIdAndUserId(1, 3)).thenReturn(Optional.of(targetMember));
        when(memberRepository.save(targetMember)).thenReturn(targetMember);
        when(groupMemberMapper.toDto(targetMember)).thenReturn(new GroupMemberDto());

        groupMemberService.updateRole(10, 1, 3, new UpdateMemberRoleInGroupTeamPayload(GroupRole.admin));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteMember_success() {
        when(memberRepository.findByGroupIdAndUserId(1, 10)).thenReturn(Optional.of(memberWithRole(1, 10, GroupRole.admin)));

        groupMemberService.deleteMember(10, 1, 5);

        verify(memberRepository).deleteByGroupIdAndUserId(1, 5);
    }

    @Test
    void deleteMember_callerNotAdmin_throws() {
        when(memberRepository.findByGroupIdAndUserId(1, 7)).thenReturn(Optional.of(memberWithRole(1, 7, GroupRole.member)));

        assertThatThrownBy(() -> groupMemberService.deleteMember(7, 1, 5))
                .isInstanceOf(NotAdminException.class);

        verify(memberRepository, never()).deleteByGroupIdAndUserId(any(), any());
    }

    private GroupMember memberWithRole(Integer groupId, Integer userId, GroupRole role) {
        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(groupId, userId));
        member.setRole(role);
        return member;
    }
}
