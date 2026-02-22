package ru.ispi.kanban.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.ispi.kanban.entity.composiveKey.BoardUserId;

@Entity
@Table(name = "board_users")
@Getter
@Setter
public class BoardUser {

    @EmbeddedId
    private BoardUserId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("boardId")
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}