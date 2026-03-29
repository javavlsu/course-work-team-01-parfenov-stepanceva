package ru.ispi.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ispi.kanban.entity.BoardUser;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;

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
}
