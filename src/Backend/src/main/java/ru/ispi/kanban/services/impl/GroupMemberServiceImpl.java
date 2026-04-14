package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.GroupMember;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.exceptions.MemberAlreadyExistsException;
import ru.ispi.kanban.exceptions.NoSuchUserByIdException;
import ru.ispi.kanban.exceptions.NotAdminException;
import ru.ispi.kanban.exceptions.NotMemberException;
import ru.ispi.kanban.listeners.AdminAssignedEvent;
import ru.ispi.kanban.mappers.GroupMemberMapper;
import ru.ispi.kanban.payloads.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payloads.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.repositories.GroupMemberRepository;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.UserRepository;
import ru.ispi.kanban.services.GroupMemberService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository memberRepository;
    private final GroupTeamRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberMapper groupMemberMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void checkAdmin(Integer groupId, Integer userId) {
        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new NotMemberException("Not a group member"));

        if (member.getRole() != GroupRole.admin) {
            throw new NotAdminException("Only group admins are allowed to perform this action");
        }
    }

    @Override
    public void checkMember(Integer groupId, Integer userId) {
        memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new NotMemberException("You are not a group member in this team, accesses denied"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Integer groupId, Integer userId) {
        return memberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void createOwner(Integer groupId, Integer userId) {
        GroupTeam groupTeam = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserByIdException("User not found: " + userId));

        memberRepository.save(createGroupMember(groupTeam, user, GroupRole.admin));
    }

    @Override
    public GroupMemberDto addMember(Integer groupId, Integer adminId, AddMemberToGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        Integer userId = payload.userId();

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new MemberAlreadyExistsException("User is already a member of this group");
        }

        GroupTeam groupTeam = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserByIdException("User not found: " + userId));

        GroupRole role = payload.role();
        GroupMember saved = memberRepository.save(createGroupMember(groupTeam, user, role));

        if (role == GroupRole.admin) {
            eventPublisher.publishEvent(new AdminAssignedEvent(groupId, user));
        }

        return groupMemberMapper.toDto(saved);
    }

    @Override
    public GroupMemberDto updateRole(Integer adminId, Integer groupId, Integer userId, UpdateMemberRoleInGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        GroupMember groupMember = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new NotMemberException("Member not found in group"));

        GroupRole oldRole = groupMember.getRole();
        GroupRole newRole = payload.role();

        groupMember.setRole(newRole);

        GroupMember saved = memberRepository.save(groupMember);

        if (oldRole != GroupRole.admin && newRole == GroupRole.admin) {
            eventPublisher.publishEvent(new AdminAssignedEvent(groupId, groupMember.getUser()));
        }

        return groupMemberMapper.toDto(saved);
    }

    @Override
    public void deleteMember(Integer adminId, Integer groupId, Integer userId) {
        checkAdmin(groupId, adminId);
        memberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberDto> getGroupMembers(Integer groupId) {
        return memberRepository.findAllByGroupId(groupId)
                .stream()
                .map(groupMemberMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupTeamDto> getUserGroups(Integer userId) {
        return memberRepository.findAllByUserId(userId)
                .stream()
                .map(member -> {
                    GroupTeam group = member.getGroup();
                    GroupTeamDto dto = new GroupTeamDto();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setDescription(group.getDescription());
                    dto.setCreatedAt(group.getCreatedAt());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAdmins(Integer groupId) {
        return memberRepository.findByGroupIdAndRole(groupId, GroupRole.admin)
                .stream()
                .map(GroupMember::getUser)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public User getMemberUser(Integer groupId, Integer userId) {
        return memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new NotMemberException("User is not a member of this group"))
                .getUser();
    }

    @Override
    public void addMemberDirectly(Integer groupId, Integer userId, GroupRole role) {
        GroupTeam group = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserByIdException("User not found: " + userId));

        memberRepository.save(createGroupMember(group, user, role));

        if (role == GroupRole.admin) {
            eventPublisher.publishEvent(new AdminAssignedEvent(groupId, user));
        }
    }

    // Вынесена логика заполнения составного ключа и сущности члена группы
    private GroupMember createGroupMember(GroupTeam group, User user, GroupRole role) {
        GroupMemberId id = new GroupMemberId();
        id.setGroupId(group.getId());
        id.setUserId(user.getId());

        GroupMember member = new GroupMember();
        member.setId(id);
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        return member;
    }
}
