package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.GroupMemberDto;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.enums.GroupRole;
import ru.ispi.kanban.payloads.AddMemberToGroupTeamPayload;
import ru.ispi.kanban.payloads.UpdateMemberRoleInGroupTeamPayload;

import java.util.List;

public interface GroupMemberService extends GroupPermissionService {

    void createOwner(Integer groupId, Integer userId);

    GroupMemberDto addMember(Integer groupId, Integer adminId, AddMemberToGroupTeamPayload payload);

    GroupMemberDto updateRole(Integer adminId, Integer groupId, Integer userId, UpdateMemberRoleInGroupTeamPayload payload);

    void deleteMember(Integer adminId, Integer groupId, Integer userId);

    List<GroupMemberDto> getGroupMembers(Integer groupId);

    List<GroupTeamDto> getUserGroups(Integer userId);

    List<User> getAdmins(Integer groupId);

    User getMemberUser(Integer groupId, Integer userId);

    void addMemberDirectly(Integer groupId, Integer userId, GroupRole role);
}
