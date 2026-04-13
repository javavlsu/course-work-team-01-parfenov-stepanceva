package ru.ispi.kanban.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.BoardUser;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.BoardUserId;
import ru.ispi.kanban.listeners.AdminAssignedEvent;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.mappers.UserMapper;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardUserService {

    private final BoardUserRepository boardUserRepository;

    private final GroupMemberService groupMemberService;

    private final UserMapper userMapper;

    private final BoardRepository boardRepository;

    public boolean hasAccess(Integer boardId, Integer userId){
        return boardUserRepository
                .existsByIdBoardIdAndIdUserId(boardId, userId);
    }

    public void checkAccess(Integer boardId, Integer userId){

        boolean access = hasAccess(boardId, userId);

        if(!access){
            throw new BoardAccessDeniedException("No access to this board");
        }
    }

    public List<Board> getUserBoards(Integer userId){
        return boardUserRepository.findByUserId(userId)
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

    public void grantAccessToAdmins(Board board) {
        List<User> admins = groupMemberService.getAdmins(board.getGroup().getId());
        if (admins.isEmpty()) return;

        // Получаем всех уже имеющих доступ одним запросом, чтобы избежать N×existsById
        Set<Integer> existingUserIds = boardUserRepository.findByBoardId(board.getId())
                .stream()
                .map(bu -> bu.getId().getUserId())
                .collect(Collectors.toSet());

        List<BoardUser> newAccesses = admins.stream()
                .filter(admin -> !existingUserIds.contains(admin.getId()))
                .map(admin -> {
                    BoardUser access = new BoardUser();
                    access.setId(new BoardUserId(board.getId(), admin.getId()));
                    access.setBoard(board);
                    access.setUser(admin);
                    return access;
                })
                .toList();

        if (!newAccesses.isEmpty()) {
            boardUserRepository.saveAll(newAccesses);
        }
    }

    public void removeAccess(Integer boardId, Integer userId){

        BoardUserId id = new BoardUserId(boardId, userId);

        boardUserRepository.deleteById(id);
    }

    public Board getUserBoard(Integer userId, Integer boardId) {

        return boardUserRepository
                .findByUserIdAndBoardId(userId, boardId)
                .map(BoardUser::getBoard)
                .orElseThrow(() -> new BoardAccessDeniedException("No access to this board"));
    }

    public List<UserDto> getUsersBoard(Integer userId, Integer boardId) {

        Board board = getUserBoard(userId, boardId);

        groupMemberService.checkMember(board.getGroup().getId(), userId);

        List<User> users = boardUserRepository.findByBoardId(boardId)
                .stream()
                .map(BoardUser::getUser)
                .toList();

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void addUserToBoard(Integer adminId, Integer boardId, Integer userId){

        Board board = getUserBoard(adminId, boardId);

        Integer groupId = board.getGroup().getId();

        groupMemberService.checkAdmin(groupId, adminId);

        User user = groupMemberService.getMemberUser(groupId, userId);

        grantAccess(board, user);
    }

    public void removeUserFromBoard(Integer adminId, Integer boardId, Integer userId){

        Board board = getUserBoard(adminId, boardId);

        Integer groupId = board.getGroup().getId();

        groupMemberService.checkAdmin(groupId, adminId);

        checkAccess(boardId, userId);

        removeAccess(boardId, userId);
    }

    public List<Board> getUserBoardsInGroup(Integer userId, Integer groupId){

        return boardUserRepository
                .findByUserIdAndBoardGroupId(userId, groupId)
                .stream()
                .map(BoardUser::getBoard)
                .toList();
    }

    public void grantAccessToAllGroupBoards(Integer groupId, User user) {
        List<Board> boards = boardRepository.findByGroupId(groupId);
        if (boards.isEmpty()) return;

        // Получаем доски группы, к которым у пользователя уже есть доступ, одним запросом
        Set<Integer> existingBoardIds = boardUserRepository.findByUserIdAndBoardGroupId(user.getId(), groupId)
                .stream()
                .map(bu -> bu.getId().getBoardId())
                .collect(Collectors.toSet());

        List<BoardUser> newAccesses = boards.stream()
                .filter(board -> !existingBoardIds.contains(board.getId()))
                .map(board -> {
                    BoardUser access = new BoardUser();
                    access.setId(new BoardUserId(board.getId(), user.getId()));
                    access.setBoard(board);
                    access.setUser(user);
                    return access;
                })
                .toList();

        if (!newAccesses.isEmpty()) {
            boardUserRepository.saveAll(newAccesses);
        }
    }

    @EventListener
    public void handleAdminAssigned(AdminAssignedEvent event) {
        grantAccessToAllGroupBoards(event.groupId(), event.user());
    }
}
