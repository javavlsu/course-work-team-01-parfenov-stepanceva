package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ispi.kanban.dto.UserDTO;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.BoardUser;
import ru.ispi.kanban.entity.User;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;

import java.util.List;
import java.util.Optional;

public interface MySqlBoardUserRepository extends JpaRepository<BoardUser, BoardUserId>
{

    boolean existsByIdBoardIdAndIdUserId(Integer boardId, Integer userId);

    @Query("""
    SELECT bu.board
    FROM BoardUser bu
    WHERE bu.user.id = :userId
    AND bu.board.group.id = :groupId
    """)
    List<Board> findBoardsByUserAndGroup(Integer userId, Integer groupId);

    @Query("""
    SELECT bu.board
    FROM BoardUser bu
    WHERE bu.user.id = :userId
    AND bu.board.id = :boardId
    AND bu.board.group.id = :groupId
    """)
    Optional<Board> findBoardByUser(Integer userId, Integer boardId, Integer groupId);

    @Query("""
    SELECT bu.user
    FROM BoardUser bu
    WHERE bu.board.id = :boardId
    """)
    List<User> findUsersByBoard(Integer boardId);

}
