package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entities.BoardUser;
import ru.ispi.kanban.entities.composiveKey.BoardUserId;

import java.util.List;
import java.util.Optional;

public interface BoardUserRepository extends JpaRepository<BoardUser, BoardUserId> {

    boolean existsByIdBoardIdAndIdUserId(Integer boardId, Integer userId);

    // board.group и board.createdBy нужны для BoardMapper.toDto
    @EntityGraph(attributePaths = {"board", "board.group", "board.createdBy"})
    List<BoardUser> findByUserIdAndBoardGroupId(Integer userId, Integer groupId);

    @EntityGraph(attributePaths = {"board", "board.group", "board.createdBy"})
    Optional<BoardUser> findByUserIdAndBoardIdAndBoardGroupId(
            Integer userId,
            Integer boardId,
            Integer groupId
    );

    @EntityGraph(attributePaths = "user")
    List<BoardUser> findByBoardId(Integer boardId);

    @EntityGraph(attributePaths = {"board", "board.group", "board.createdBy"})
    List<BoardUser> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"board", "board.group", "board.createdBy"})
    Optional<BoardUser> findByUserIdAndBoardId(Integer userId, Integer boardId);
}
