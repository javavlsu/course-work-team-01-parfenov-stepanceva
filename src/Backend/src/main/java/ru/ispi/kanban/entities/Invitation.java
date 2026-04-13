package ru.ispi.kanban.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.ispi.kanban.enums.InvitationStatus;
import ru.ispi.kanban.enums.InvitationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter
@Setter
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupTeam group;

    /** null для приглашений типа LINK */
    @Column(nullable = true)
    private String email;

    /** UUID-токен: для EMAIL — скрытая часть ссылки, для LINK — публичная ссылка */
    @Column(nullable = false, unique = true)
    private String inviteToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationType type;

    /** Актуально для EMAIL-приглашений; для LINK всегда PENDING до истечения срока */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
