package ru.ispi.kanban.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import ru.ispi.kanban.entity.composiveKey.GroupMemberId;
import ru.ispi.kanban.enums.GroupRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Getter
@Setter
public class GroupMember {

    @EmbeddedId
    private GroupMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private GroupTeam group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
