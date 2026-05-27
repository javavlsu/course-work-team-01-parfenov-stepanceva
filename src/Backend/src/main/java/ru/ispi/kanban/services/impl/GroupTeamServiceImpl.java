package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ispi.kanban.dto.GroupTeamDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.mappers.GroupTeamMapper;
import ru.ispi.kanban.payloads.GroupTeamPayload;
import ru.ispi.kanban.repositories.AttachmentRepository;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.repositories.ColumnRepository;
import ru.ispi.kanban.repositories.CommentRepository;
import ru.ispi.kanban.repositories.GroupMemberRepository;
import ru.ispi.kanban.repositories.GroupTeamRepository;
import ru.ispi.kanban.repositories.InvitationRepository;
import ru.ispi.kanban.repositories.TaskHistoryRepository;
import ru.ispi.kanban.repositories.TaskRepository;
import ru.ispi.kanban.services.FileStorageService;
import ru.ispi.kanban.services.GroupMemberService;
import ru.ispi.kanban.services.GroupTeamService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupTeamServiceImpl implements GroupTeamService {

    private final GroupTeamRepository mySqlGroupTeamRepository;
    private final GroupMemberService groupMemberService;
    private final GroupTeamMapper groupTeamMapper;
    private final BoardRepository boardRepository;
    private final InvitationRepository invitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final BoardUserRepository boardUserRepository;
    private final FileStorageService fileStorageService;

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
    @Transactional
    public void delete(Integer groupId, Integer userId) {
        groupMemberService.checkAdmin(groupId, userId);

        List<Board> boards = boardRepository.findByGroupId(groupId);
        boards.forEach(b -> {
            Integer boardId = b.getId();
            attachmentRepository.findAllByTask_Column_Board_Id(boardId)
                    .forEach(a -> fileStorageService.deleteFile(a.getStorageKey()));
            taskHistoryRepository.deleteAllByTask_Column_Board_Id(boardId);
            commentRepository.deleteAllByTask_Column_Board_Id(boardId);
            attachmentRepository.deleteAllByTask_Column_Board_Id(boardId);
            taskRepository.deleteAllByColumn_Board_Id(boardId);
            columnRepository.deleteAllByBoardId(boardId);
            boardUserRepository.deleteAllByBoardId(boardId);
        });
        boardRepository.deleteAll(boards);

        invitationRepository.deleteAllByGroupId(groupId);
        groupMemberRepository.deleteAllByGroupId(groupId);
        mySqlGroupTeamRepository.deleteById(groupId);
    }

    @Override
    public GroupTeam getEntity(Integer groupId, Integer userId) {
        groupMemberService.checkMember(groupId, userId);
        return mySqlGroupTeamRepository.getReferenceById(groupId);
    }
}
