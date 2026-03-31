package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.BoardDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.GroupTeam;
import ru.ispi.kanban.mappers.BoardMapper;
import ru.ispi.kanban.payloads.CreateBoardPayload;
import ru.ispi.kanban.payloads.UpdateBoardPayload;
import ru.ispi.kanban.repositories.BoardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final GroupMemberService groupMemberService;

    private final BoardUserService boardUserService;

    private final BoardRepository boardRepository;

    private final GroupTeamService groupTeamService;

    private final BoardMapper boardMapper;

    public List<BoardDto> getUserBoards(Integer userId) {

        List<Board> boards = boardUserService.getUserBoards(userId);

        return boards.stream()
                .map(boardMapper::toDto)
                .toList();
    }

    public BoardDto getUserBoard(Integer userId, Integer boardId) {

        Board board = boardUserService.getUserBoard(userId, boardId);

        return boardMapper.toDto(board);
    }

    public BoardDto create(CreateBoardPayload payload, Integer userId) {

        Integer groupId = payload.groupId();

        groupMemberService.checkAdmin(groupId, userId);

        GroupTeam group = groupTeamService.getEntity(groupId, userId);

        Board board = boardMapper.toEntity(payload);
        board.setGroup(group);

        Board saved = boardRepository.save(board);

        boardUserService.grantAccessToAdmins(saved);

        return boardMapper.toDto(saved);
    }

    public BoardDto update(UpdateBoardPayload payload, Integer userId, Integer boardId) {

        Board board = boardUserService.getUserBoard(userId, boardId);

        groupMemberService.checkAdmin(board.getGroup().getId(), userId);

        boardMapper.update(board, payload);

        return boardMapper.toDto(boardRepository.save(board));
    }

    public void delete(Integer userId, Integer boardId) {

        Board board = boardUserService.getUserBoard(userId, boardId);

        groupMemberService.checkAdmin(board.getGroup().getId(), userId);

        boardRepository.delete(board);
    }

    public List<BoardDto> getUserBoardsInGroup(Integer userId, Integer groupId){

        groupMemberService.checkMember(groupId, userId);

        List<Board> boards = boardUserService.getUserBoardsInGroup(userId, groupId);

        return boards.stream()
                .map(boardMapper::toDto)
                .toList();
    }
}
