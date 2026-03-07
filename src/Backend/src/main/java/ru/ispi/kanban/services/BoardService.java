package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.BoardDTO;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.GroupTeam;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.payload.BoardPayload;
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

    public List<BoardDTO> getUserBoards(Integer userId, Integer groupId){

        groupMemberService.checkMember(groupId, userId);

        List<Board> boards =
                boardUserService.getUserBoards(userId, groupId);

        return boards.stream()
                .map(this::convertToDto)
                .toList();
    }

    public BoardDTO create(BoardPayload payload, Integer userId, Integer groupId){

        groupMemberService.checkAdmin(groupId, userId);

        User creator = userService.getEntity(userId);
        GroupTeam group = groupTeamService.getEntity(groupId, userId);

        Board board = new Board();
        board.setTitle(payload.title());
        board.setDescription(payload.description());
        board.setGroup(group);
        board.setCreatedBy(creator);

        Board savedBoard = boardRepository.save(board);

        boardUserService.grantAccessToAdmins(savedBoard);

        return convertToDto(savedBoard);
    }

    private BoardDTO convertToDto(Board board){
        return new BoardDTO(
                board.getId(),
                board.getGroup(),
                board.getTitle(),
                board.getDescription(),
                board.getCreatedBy(),
                board.getCreatedAt()
        );
    }
}
