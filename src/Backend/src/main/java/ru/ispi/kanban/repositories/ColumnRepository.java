package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE BoardColumn c SET c.position = c.position - 1 " +
            "WHERE c.board.id = :boardId AND c.position > :oldPos AND c.position <= :newPos")
    void shiftForward(Integer boardId, Long oldPos, Long newPos);

    @Modifying
    @Query("UPDATE BoardColumn c SET c.position = c.position + 1 " +
            "WHERE c.board.id = :boardId AND c.position >= :newPos AND c.position < :oldPos")
    void shiftBackward(Integer boardId, Long oldPos, Long newPos);

    @Modifying
    @Query("UPDATE BoardColumn c SET c.position = c.position - 1 " +
            "WHERE c.board.id = :boardId AND c.position > :deletedPos")
    void shiftAfterDelete(Integer boardId, Long deletedPos);
}