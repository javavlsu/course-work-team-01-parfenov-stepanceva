package ru.ispi.kanban.services;

import ru.ispi.kanban.dto.UserDto;
import ru.ispi.kanban.entities.Board;

import java.util.List;

public interface BoardUserService {

    List<Board> getUserBoards(Integer userId);

    Board getUserBoard(Integer userId, Integer boardId);

    void grantAccessToAdmins(Board board);

    List<Board> getUserBoardsInGroup(Integer userId, Integer groupId);

    List<UserDto> getUsersBoard(Integer userId, Integer boardId);

    void addUserToBoard(Integer adminId, Integer boardId, Integer userId);

    void removeUserFromBoard(Integer adminId, Integer boardId, Integer userId);
}
