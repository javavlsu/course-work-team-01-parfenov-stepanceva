package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.BoardUser;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;
import ru.ispi.kanban.mapper.UserMapper;
import ru.ispi.kanban.repository.BoardUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardUserService {

    private final BoardUserRepository boardUserRepository;

    private final GroupMemberService groupMemberService;

    private final UserMapper userMapper;

    public boolean hasAccess(Integer boardId, Integer userId){
        return boardUserRepository
                .existsByIdBoardIdAndIdUserId(boardId, userId);
    }

    public void checkAccess(Integer boardId, Integer userId){

        boolean access = hasAccess(boardId, userId);

        if(!access){
            throw new RuntimeException("No access to this board");
        }
    }

    public List<Board> getUserBoards(Integer userId, Integer groupId){
        return boardUserRepository.findByUserIdAndBoardGroupId(userId, groupId)
                .stream()
                .map(BoardUser::getBoard)
                .toList();
    }

    public void grantAccess(Board board, User user){

        BoardUserId id = new BoardUserId(
                board.getId(),
                user.getId()
        );

        if(boardUserRepository.existsById(id)){
            return;
        }

        BoardUser boardUser = new BoardUser();
        boardUser.setId(id);
        boardUser.setBoard(board);
        boardUser.setUser(user);

        boardUserRepository.save(boardUser);
    }

    public void grantAccessToAdmins(Board board){

        List<User> admins =
                groupMemberService.getAdmins(board.getGroup().getId());

        for(User admin : admins){

            BoardUserId id =
                    new BoardUserId(board.getId(), admin.getId());

            if(boardUserRepository.existsById(id)){
                continue;
            }

            BoardUser access = new BoardUser();
            access.setId(id);
            access.setBoard(board);
            access.setUser(admin);

            boardUserRepository.save(access);
        }
    }

    public void removeAccess(Integer boardId, Integer userId){

        BoardUserId id = new BoardUserId(boardId, userId);

        boardUserRepository.deleteById(id);
    }

    public Board getUserBoard(Integer userIdFromToken, Integer groupId, Integer boardId) {

        return boardUserRepository
                .findByUserIdAndBoardIdAndBoardGroupId(userIdFromToken, boardId, groupId)
                .map(BoardUser::getBoard)
                .orElseThrow(() -> new RuntimeException("Доска не найдена или у вас нет доступа к ней"));
    }

    public List<UserDto> getUsersBoard(Integer userIdFromToken, Integer groupId, Integer boardId) {

        groupMemberService.checkMember(groupId, userIdFromToken);

        checkAccess(boardId, userIdFromToken);

        List<User> users = boardUserRepository.findByBoardId(boardId)
                .stream()
                .map(BoardUser::getUser)
                .toList();

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void addUserToBoard(Integer adminId, Integer groupId, Integer boardId, Integer userId){

        groupMemberService.checkAdmin(groupId, adminId);

        Board board = getUserBoard(adminId, groupId, boardId);

        User user = groupMemberService.getMemberUser(groupId, userId);

        grantAccess(board, user);
    }

    public void removeUserFromBoard(Integer adminId, Integer groupId, Integer boardId, Integer userId){

        groupMemberService.checkAdmin(groupId, adminId);

        checkAccess(boardId, userId);

        removeAccess(boardId, userId);
    }
}
