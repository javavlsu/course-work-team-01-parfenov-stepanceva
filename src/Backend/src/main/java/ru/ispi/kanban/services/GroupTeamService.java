package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.mapper.GroupTeamMapper;
import ru.ispi.kanban.payload.GroupTeamPayload;
import ru.ispi.kanban.repository.GroupTeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTeamService {

    private final GroupTeamRepository mySqlGroupTeamRepository;

    private final GroupMemberService groupMemberService;

    private final GroupTeamMapper groupTeamMapper;

    public List<GroupTeam> getGroupTeams() {
        return mySqlGroupTeamRepository.findAll();
    }

    public List<GroupTeamDto> getUserGroups(Integer userId) {

        return groupMemberService.getUserGroups(userId);
    }

    public GroupTeamDto get(Integer groupId, Integer userId) {

        groupMemberService.checkMember(groupId, userId);

        return groupTeamMapper.toDto(mySqlGroupTeamRepository.getReferenceById(groupId));
    }

    public GroupTeamDto create(GroupTeamPayload payload, Integer creatorId) {
        GroupTeam groupTeam = new GroupTeam();
        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());


        GroupTeam savedGroupTeam = mySqlGroupTeamRepository.save(groupTeam);

        groupMemberService.createOwner(
                savedGroupTeam.getId(),
                creatorId
        );

        return groupTeamMapper.toDto(savedGroupTeam);
    }

    public GroupTeamDto update(Integer groupId,
                               Integer userId,
                               GroupTeamPayload payload) {

        groupMemberService.checkAdmin(groupId, userId);

        GroupTeam groupTeam = mySqlGroupTeamRepository.getReferenceById(groupId);

        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());

        return groupTeamMapper.toDto(
                mySqlGroupTeamRepository.save(groupTeam)
        );
    }

    public void delete(Integer groupId, Integer userId) {

        groupMemberService.checkAdmin(groupId, userId);

        mySqlGroupTeamRepository.deleteById(groupId);
    }


    public GroupTeam getEntity(Integer groupId, Integer userId) {

        groupMemberService.checkMember(groupId, userId);

        return mySqlGroupTeamRepository.getReferenceById(groupId);
    }
}
