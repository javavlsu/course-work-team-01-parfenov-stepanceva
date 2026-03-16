package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entity.Board;

import java.util.List;

public interface MySqlBoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByGroupId(Integer groupId);

}