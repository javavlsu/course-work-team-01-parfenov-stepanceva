package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entity.Board;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;

import java.util.List;

public interface MySqlBoardRepository extends JpaRepository<Board, BoardUserId>
{
    List<Board> findByGroupId(Integer groupId);
}
