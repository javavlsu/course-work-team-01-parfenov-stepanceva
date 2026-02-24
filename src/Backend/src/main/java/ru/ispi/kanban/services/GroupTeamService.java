package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.GroupTeamDTO;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.payload.GroupTeamPayload;
import ru.ispi.kanban.repository.MySqlGroupTeamRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTeamService {

    private final MySqlGroupTeamRepository mySqlGroupTeamRepository;

    private final GroupMemberService groupMemberService;

    public List<GroupTeam> getGroupTeams() {
        return mySqlGroupTeamRepository.findAll();
    }

    public List<GroupTeamDTO> getUserGroups(Integer userId) {

        return groupMemberService.getUserGroups(userId);
    }

    public GroupTeamDTO get(Integer groupId, Integer userId) {

        groupMemberService.checkMember(groupId, userId);

        return convertToDto(mySqlGroupTeamRepository.getReferenceById(groupId));
    }

    public GroupTeamDTO create(GroupTeamPayload payload, Integer creatorId) {
        GroupTeam groupTeam = new GroupTeam();
        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());
        groupTeam.setCreatedAt(LocalDateTime.now());

        GroupTeam savedGroupTeam = mySqlGroupTeamRepository.save(groupTeam);

        groupMemberService.createOwner(
                savedGroupTeam.getId(),
                creatorId
        );

        return convertToDto(savedGroupTeam);
    }

    public GroupTeamDTO update(Integer groupId,
                               Integer userId,
                               GroupTeamPayload payload) {

        groupMemberService.checkAdmin(groupId, userId);

        GroupTeam groupTeam = mySqlGroupTeamRepository.getReferenceById(groupId);

        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());

        return convertToDto(
                mySqlGroupTeamRepository.save(groupTeam)
        );
    }

    public void delete(Integer groupId, Integer userId) {

        groupMemberService.checkAdmin(groupId, userId);

        mySqlGroupTeamRepository.deleteById(groupId);
    }

    private GroupTeamDTO convertToDto(GroupTeam savedGroupTeam) {
        GroupTeamDTO groupTeamDTO = new GroupTeamDTO();
        groupTeamDTO.setId(savedGroupTeam.getId());
        groupTeamDTO.setName(savedGroupTeam.getName());
        groupTeamDTO.setDescription(savedGroupTeam.getDescription());
        groupTeamDTO.setCreatedAt(savedGroupTeam.getCreatedAt());
        return groupTeamDTO;
    }
}
