package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entities.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByGroupId(Integer groupId);

}