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

    public List<GroupTeam> getGroupTeams() {
        return mySqlGroupTeamRepository.findAll();
    }

    public GroupTeam get(Integer id) {
        return mySqlGroupTeamRepository.getReferenceById(id);
    }

    public GroupTeamDTO create(GroupTeamPayload payload) {
        GroupTeam groupTeam = new GroupTeam();
        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());
        groupTeam.setCreatedAt(LocalDateTime.now());

        GroupTeam savedGroupTeam = mySqlGroupTeamRepository.save(groupTeam);
        return convertToDto(savedGroupTeam);
    }

    public GroupTeamDTO update(Integer id, GroupTeamPayload payload) {
        GroupTeam groupTeam = get(id);

        groupTeam.setName(payload.name());
        groupTeam.setDescription(payload.description());

        GroupTeam updatedGroupTeam = mySqlGroupTeamRepository.save(groupTeam);
        return convertToDto(updatedGroupTeam);
    }

    public void delete(Integer id) {
        GroupTeam groupTeam = get(id);
        mySqlGroupTeamRepository.delete(groupTeam);
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
