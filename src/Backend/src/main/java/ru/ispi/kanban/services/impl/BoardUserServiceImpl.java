package ru.ispi.kanban.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.Board;
import ru.ispi.kanban.entities.BoardUser;
import ru.ispi.kanban.entities.User;
import ru.ispi.kanban.entities.composiveKey.BoardUserId;
import ru.ispi.kanban.exceptions.BoardAccessDeniedException;
import ru.ispi.kanban.listeners.AdminAssignedEvent;
import ru.ispi.kanban.mappers.UserMapper;
import ru.ispi.kanban.repositories.BoardRepository;
import ru.ispi.kanban.repositories.BoardUserRepository;
import ru.ispi.kanban.services.BoardUserService;
import ru.ispi.kanban.services.GroupMemberService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardUserServiceImpl implements BoardUserService {

    private final BoardUserRepository boardUserRepository;
    private final GroupMemberService groupMemberService;
    private final UserMapper userMapper;
    private final BoardRepository boardRepository;

    @Override
    public List<Board> getUserBoards(Integer userId) {
        return boardUserRepository.findByUserId(userId)
                .stream()
                .map(BoardUser::getBoard)
                .toList();
    }

    @Override
    public Board getUserBoard(Integer userId, Integer boardId) {
        return boardUserRepository
                .findByUserIdAndBoardId(userId, boardId)
                .map(BoardUser::getBoard)
                .orElseThrow(() -> new BoardAccessDeniedException("No access to this board"));
    }

    @Override
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
                .map(admin -> buildBoardUser(board, admin))
                .toList();

        if (!newAccesses.isEmpty()) {
            boardUserRepository.saveAll(newAccesses);
        }
    }

    @Override
    public List<Board> getUserBoardsInGroup(Integer userId, Integer groupId) {
        return boardUserRepository
                .findByUserIdAndBoardGroupId(userId, groupId)
                .stream()
                .map(BoardUser::getBoard)
                .toList();
    }

    @Override
    public List<UserDto> getUsersBoard(Integer userId, Integer boardId) {
        Board board = getUserBoard(userId, boardId);

        groupMemberService.checkMember(board.getGroup().getId(), userId);

        return boardUserRepository.findByBoardId(boardId)
                .stream()
                .map(BoardUser::getUser)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void addUserToBoard(Integer adminId, Integer boardId, Integer userId) {
        Board board = getUserBoard(adminId, boardId);
        Integer groupId = board.getGroup().getId();

        groupMemberService.checkAdmin(groupId, adminId);

        User user = groupMemberService.getMemberUser(groupId, userId);
        grantAccess(board, user);
    }

    @Override
    public void removeUserFromBoard(Integer adminId, Integer boardId, Integer userId) {
        Board board = getUserBoard(adminId, boardId);
        Integer groupId = board.getGroup().getId();

        groupMemberService.checkAdmin(groupId, adminId);
        checkAccess(boardId, userId);
        removeAccess(boardId, userId);
    }

    // Слушает событие AdminAssignedEvent и выдаёт новому админу доступ ко всем доскам группы
    @EventListener
    public void handleAdminAssigned(AdminAssignedEvent event) {
        grantAccessToAllGroupBoards(event.groupId(), event.user());
    }

    private void grantAccessToAllGroupBoards(Integer groupId, User user) {
        List<Board> boards = boardRepository.findByGroupId(groupId);
        if (boards.isEmpty()) return;

        // Получаем доски группы, к которым у пользователя уже есть доступ, одним запросом
        Set<Integer> existingBoardIds = boardUserRepository.findByUserIdAndBoardGroupId(user.getId(), groupId)
                .stream()
                .map(bu -> bu.getId().getBoardId())
                .collect(Collectors.toSet());

        List<BoardUser> newAccesses = boards.stream()
                .filter(board -> !existingBoardIds.contains(board.getId()))
                .map(board -> buildBoardUser(board, user))
                .toList();

        if (!newAccesses.isEmpty()) {
            boardUserRepository.saveAll(newAccesses);
        }
    }

    private void grantAccess(Board board, User user) {
        BoardUserId id = new BoardUserId(board.getId(), user.getId());

        if (boardUserRepository.existsById(id)) return;

        boardUserRepository.save(buildBoardUser(board, user));
    }

    private void removeAccess(Integer boardId, Integer userId) {
        boardUserRepository.deleteById(new BoardUserId(boardId, userId));
    }

    private void checkAccess(Integer boardId, Integer userId) {
        if (!boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId)) {
            throw new BoardAccessDeniedException("No access to this board");
        }
    }

    private boolean hasAccess(Integer boardId, Integer userId) {
        return boardUserRepository.existsByIdBoardIdAndIdUserId(boardId, userId);
    }

    private BoardUser buildBoardUser(Board board, User user) {
        BoardUser bu = new BoardUser();
        bu.setId(new BoardUserId(board.getId(), user.getId()));
        bu.setBoard(board);
        bu.setUser(user);
        return bu;
    }
}
