package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.payloads.GroupTeamPayload;

import java.util.List;

public interface GroupTeamService {

    List<GroupTeamDto> getUserGroups(Integer userId);

    GroupTeamDto get(Integer groupId, Integer userId);

    GroupTeamDto create(GroupTeamPayload payload, Integer creatorId);

    GroupTeamDto update(Integer groupId, Integer userId, GroupTeamPayload payload);

    void delete(Integer groupId, Integer userId);

    GroupTeam getEntity(Integer groupId, Integer userId);
}
