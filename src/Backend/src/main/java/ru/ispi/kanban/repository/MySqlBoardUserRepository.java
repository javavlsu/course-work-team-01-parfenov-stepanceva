package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.BoardUser;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;

import java.util.List;

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

}
