package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.BoardDTO;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.mapper.BoardMapper;
import ru.ispi.kanban.payload.CreateBoardPayload;
import ru.ispi.kanban.payload.UpdateBoardPayload;
import ru.ispi.kanban.repository.MySqlBoardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final GroupMemberService groupMemberService;

    private final BoardUserService boardUserService;

    private final MySqlBoardRepository boardRepository;

    private final UserService userService;

    private final GroupTeamService groupTeamService;

    private final BoardMapper boardMapper;

    public List<BoardDTO> getUserBoards(Integer userIdFromToken, Integer groupId){

        groupMemberService.checkMember(groupId, userIdFromToken);

        List<Board> boards =
                boardUserService.getUserBoards(userIdFromToken, groupId);

        return boards.stream()
                .map(boardMapper::toDto)
                .toList();
    }

    public BoardDTO create(CreateBoardPayload payload, Integer userIdFromToken, Integer groupId){

        groupMemberService.checkAdmin(groupId, userIdFromToken);

        User creator = userService.getEntity(userIdFromToken);
        GroupTeam group = groupTeamService.getEntity(groupId, userIdFromToken);

        Board board = new Board();
        board.setTitle(payload.title());
        board.setDescription(payload.description());
        board.setGroup(group);
        board.setCreatedBy(creator);

        Board savedBoard = boardRepository.save(board);

        boardUserService.grantAccessToAdmins(savedBoard);

        return boardMapper.toDto(savedBoard);
    }

    public BoardDTO getUserBoard(Integer userIdFromToken, Integer groupId, Integer boardId) {

        groupMemberService.checkMember(groupId, userIdFromToken);

        Board board =
                boardUserService.getUserBoard(userIdFromToken, groupId, boardId);

        return boardMapper.toDto(board);
    }

    public BoardDTO update(UpdateBoardPayload payload, Integer userIdFromToken, Integer groupId, Integer boardId) {

        groupMemberService.checkAdmin(groupId, userIdFromToken);

        Board board =
                boardUserService.getUserBoard(userIdFromToken, groupId, boardId);

        board.setTitle(payload.title());
        board.setDescription(payload.description());

        Board saved = boardRepository.save(board);

        return boardMapper.toDto(saved);
    }

    public void delete(Integer userIdFromToken, Integer groupId, Integer boardId) {

        groupMemberService.checkAdmin(groupId, userIdFromToken);

        Board board =
                boardUserService.getUserBoard(userIdFromToken, groupId, boardId);

        boardRepository.delete(board);
    }
}
