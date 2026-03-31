package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.GroupMember;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.exceptions.NotMemberException;
import ru.ispi.kanban.mappers.GroupMemberMapper;
import ru.ispi.kanban.payloads.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payloads.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.repositories.GroupMemberRepository;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberService {

    private final GroupMemberRepository memberRepository;

    private final GroupTeamRepository groupRepository;

    private final UserRepository userRepository;

    private final GroupMemberMapper groupMemberMapper;

    public void checkAdmin(Integer groupId, Integer userId) {

        GroupMember groupMember = memberRepository.findByGroupIdAndUserId(
                groupId, userId
        ).orElseThrow(
                () -> new RuntimeException("Not group member")
                );

        if (groupMember.getRole() != GroupRole.admin) {
            throw new RuntimeException("Only group admins allowed");
        }
    }

    public void createOwner(Integer groupId, Integer userId) {

        GroupTeam groupTeam = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        GroupMemberId groupMemberId = new GroupMemberId();

        groupMemberId.setGroupId(groupId);
        groupMemberId.setUserId(userId);

        GroupMember groupMember = createGroupMember(groupTeam, user, GroupRole.admin);

        memberRepository.save(groupMember);
    }

    public GroupMemberDto addMember(Integer groupId, Integer adminId, AddMemberToGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        Integer userId = payload.userId();

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {

            throw new RuntimeException("Member already exists");

        }

        GroupTeam groupTeam = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        GroupRole role = payload.role();
        GroupMember member = createGroupMember(groupTeam, user, role);

        return groupMemberMapper.toDto(memberRepository.save(member));
    }

    public GroupMemberDto updateRole(Integer adminId, Integer groupId, Integer userId, UpdateMemberRoleInGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        GroupMember groupMember = memberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(
                () -> new RuntimeException("Member not found")
        );

        groupMember.setRole(payload.role());

        return groupMemberMapper.toDto(memberRepository.save(groupMember));
    }

    public void deleteMember(Integer adminId, Integer groupId, Integer userId) {
        checkAdmin(groupId, adminId);

        memberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberDto> getGroupMembers(Integer groupId) {

        return memberRepository.findAllByGroupId(groupId)
                .stream()
                .map(groupMemberMapper ::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

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

    public void checkMember(Integer groupId, Integer userId) {

        memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() ->
                        new NotMemberException("You are not a group member in this team, accesses denied"));
    }

    public List<User> getAdmins(Integer groupId){
        return memberRepository.findByGroupIdAndRole(groupId, GroupRole.admin)
                .stream()
                .map(GroupMember::getUser)
                .toList();
    }


    //вынес логику заполнения
    private GroupMember createGroupMember(GroupTeam group, User user, GroupRole role) {
        // Создаем составной ключ
        GroupMemberId id = new GroupMemberId();
        id.setGroupId(group.getId());
        id.setUserId(user.getId());

        // Создаем и заполняем сущность
        GroupMember member = new GroupMember();
        member.setId(id);
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        return member;
    }

    @Transactional(readOnly = true)
    public User getMemberUser(Integer groupId, Integer userId) {

        GroupMember member = memberRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() ->
                        new NotMemberException("User is not a member of this group"));

        return member.getUser();
    }
}
