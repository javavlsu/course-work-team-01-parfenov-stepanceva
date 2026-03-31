package ru.ispi.kanban.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entities.BoardUser;
import ru.ispi.kanban.entities.composiveKey.BoardUserId;

import java.util.List;
import java.util.Optional;

public interface BoardUserRepository extends JpaRepository<BoardUser, BoardUserId> {

    boolean existsByIdBoardIdAndIdUserId(Integer boardId, Integer userId);

    List<BoardUser> findByUserIdAndBoardGroupId(Integer userId, Integer groupId);

    Optional<BoardUser> findByUserIdAndBoardIdAndBoardGroupId(
            Integer userId,
            Integer boardId,
            Integer groupId
    );

    List<BoardUser> findByBoardId(Integer boardId);

    List<BoardUser> findByUserId(Integer userId);

    Optional<BoardUser> findByUserIdAndBoardId(Integer userId, Integer boardId);
}
