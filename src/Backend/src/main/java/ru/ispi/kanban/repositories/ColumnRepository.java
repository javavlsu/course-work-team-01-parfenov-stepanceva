package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ispi.kanban.entities.BoardColumn;

import java.util.List;
import java.util.Optional;


public interface ColumnRepository extends JpaRepository<BoardColumn, Integer> {

    List<BoardColumn> findAllByBoardIdOrderByPositionAsc(Integer boardId);

    Optional<BoardColumn> findByIdAndBoardId(Integer columnId, Integer boardId);

    @Query("""
        SELECT COALESCE(MAX(c.position), 0)
        FROM BoardColumn c
        WHERE c.board.id = :boardId
    """)
    Long findMaxPosition(Integer boardId);

    boolean existsByIdAndBoardId(Integer columnId, Integer boardId);
}