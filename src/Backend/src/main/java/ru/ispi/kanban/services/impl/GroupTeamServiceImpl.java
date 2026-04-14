package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.mappers.GroupTeamMapper;
import ru.ispi.kanban.payloads.GroupTeamPayload;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.services.GroupMemberService;
import ru.ispi.kanban.services.GroupTeamService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTeamServiceImpl implements GroupTeamService {

    private final GroupTeamRepository mySqlGroupTeamRepository;
    private final GroupMemberService groupMemberService;
    private final GroupTeamMapper groupTeamMapper;

    @Override
    public List<GroupTeamDto> getUserGroups(Integer userId) {
        return groupMemberService.getUserGroups(userId);
    }

    @Override
    public GroupTeamDto get(Integer groupId, Integer userId) {
        groupMemberService.checkMember(groupId, userId);
        return groupTeamMapper.toDto(mySqlGroupTeamRepository.getReferenceById(groupId));
    }

    @Override
    public GroupTeamDto create(GroupTeamPayload payload, Integer creatorId) {
        GroupTeam groupTeam = groupTeamMapper.toEntity(payload);
        GroupTeam savedGroupTeam = mySqlGroupTeamRepository.save(groupTeam);

        groupMemberService.createOwner(savedGroupTeam.getId(), creatorId);

        return groupTeamMapper.toDto(savedGroupTeam);
    }

    @Override
    public GroupTeamDto update(Integer groupId, Integer userId, GroupTeamPayload payload) {
        groupMemberService.checkAdmin(groupId, userId);

        GroupTeam groupTeam = mySqlGroupTeamRepository.getReferenceById(groupId);
        groupTeamMapper.update(groupTeam, payload);

        return groupTeamMapper.toDto(mySqlGroupTeamRepository.save(groupTeam));
    }

    @Override
    public void delete(Integer groupId, Integer userId) {
        groupMemberService.checkAdmin(groupId, userId);
        mySqlGroupTeamRepository.deleteById(groupId);
    }

    @Override
    public GroupTeam getEntity(Integer groupId, Integer userId) {
        groupMemberService.checkMember(groupId, userId);
        return mySqlGroupTeamRepository.getReferenceById(groupId);
    }
}
