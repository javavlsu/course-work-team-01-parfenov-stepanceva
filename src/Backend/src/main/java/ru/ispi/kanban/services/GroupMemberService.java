package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.GroupMemberDTO;
import ru.ispi.kanban.dto.GroupTeamDTO;
import ru.ispi.kanban.entity.GroupMember;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.entity.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.payload.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payload.UpdateMemberRoleInGroupTeamPayload;
import ru.ispi.kanban.repository.MySqlGroupMemberRepository;
import ru.ispi.kanban.repository.MySqlGroupTeamRepository;
import ru.ispi.kanban.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberService {

    private final MySqlGroupMemberRepository memberRepository;

    private final MySqlGroupTeamRepository groupRepository;

    private final UserRepository userRepository;

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

    public GroupMemberDTO addMember(Integer groupId, Integer adminId, AddMemberToGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        Integer userId = payload.userId();

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {

            throw new RuntimeException("Member already exists");

        }

        GroupTeam groupTeam = groupRepository.getReferenceById(groupId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        GroupRole role = GroupRole.valueOf(payload.role());
        GroupMember member = createGroupMember(groupTeam, user, role);

        return convertToDto(memberRepository.save(member));
    }

    public GroupMemberDTO updateRole(Integer adminId, Integer groupId, Integer userId, UpdateMemberRoleInGroupTeamPayload payload) {
        checkAdmin(groupId, adminId);

        GroupMember groupMember = memberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(
                () -> new RuntimeException("Member not found")
        );

        groupMember.setRole(GroupRole.valueOf(payload.role()));

        return convertToDto(memberRepository.save(groupMember));
    }

    public void deleteMember(Integer adminId, Integer groupId, Integer userId) {
        checkAdmin(groupId, adminId);

        memberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberDTO> getGroupMembers(Integer groupId) {

        return memberRepository.findAllByGroupId(groupId)
                .stream()
                .map(this ::convertToDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public List<GroupTeamDTO> getUserGroups(Integer userId) {

        return memberRepository.findAllByUserId(userId)
                .stream()
                .map(member -> {
                    GroupTeam group = member.getGroup();

                    GroupTeamDTO dto = new GroupTeamDTO();
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
                        new RuntimeException("Access denied"));
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
        member.setJoinedAt(LocalDateTime.now());

        return member;
    }

    private GroupMemberDTO convertToDto(GroupMember member) {
        GroupMemberDTO dto = new GroupMemberDTO();
        dto.setGroupId(member.getGroup().getId());
        dto.setUserId(member.getUser().getId());
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());

        return dto;
    }
}
