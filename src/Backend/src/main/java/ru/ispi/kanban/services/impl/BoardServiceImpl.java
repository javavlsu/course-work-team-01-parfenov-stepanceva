package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.mappers.BoardMapper;
import ru.ispi.kanban.payloads.CreateBoardPayload;
import ru.ispi.kanban.payloads.UpdateBoardPayload;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.services.BoardService;
import ru.ispi.kanban.services.BoardUserService;
import ru.ispi.kanban.services.GroupPermissionService;
import ru.ispi.kanban.services.GroupTeamService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    // ISP: BoardService использует только проверку прав — достаточно GroupPermissionService
    private final GroupPermissionService groupPermissionService;
    private final BoardUserService boardUserService;
    private final BoardRepository boardRepository;
    private final GroupTeamService groupTeamService;
    private final BoardMapper boardMapper;

    @Override
    public List<BoardDto> getUserBoards(Integer userId) {
        return boardUserService.getUserBoards(userId)
                .stream()
                .map(boardMapper::toDto)
                .toList();
    }

    @Override
    public BoardDto getUserBoard(Integer userId, Integer boardId) {
        return boardMapper.toDto(boardUserService.getUserBoard(userId, boardId));
    }

    @Override
    public BoardDto create(CreateBoardPayload payload, Integer userId) {
        Integer groupId = payload.groupId();

        groupPermissionService.checkAdmin(groupId, userId);

        GroupTeam group = groupTeamService.getEntity(groupId, userId);

        Board board = boardMapper.toEntity(payload);
        board.setGroup(group);

        Board saved = boardRepository.save(board);

        boardUserService.grantAccessToAdmins(saved);

        return boardMapper.toDto(saved);
    }

    @Override
    public BoardDto update(UpdateBoardPayload payload, Integer userId, Integer boardId) {
        Board board = boardUserService.getUserBoard(userId, boardId);

        groupPermissionService.checkAdmin(board.getGroup().getId(), userId);

        boardMapper.update(board, payload);

        return boardMapper.toDto(boardRepository.save(board));
    }

    @Override
    public void delete(Integer userId, Integer boardId) {
        Board board = boardUserService.getUserBoard(userId, boardId);

        groupPermissionService.checkAdmin(board.getGroup().getId(), userId);

        boardRepository.delete(board);
    }

    @Override
    public List<BoardDto> getUserBoardsInGroup(Integer userId, Integer groupId) {
        groupPermissionService.checkMember(groupId, userId);

        return boardUserService.getUserBoardsInGroup(userId, groupId)
                .stream()
                .map(boardMapper::toDto)
                .toList();
    }
}
