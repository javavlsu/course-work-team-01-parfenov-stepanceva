package ru.ispi.kanban.entity;


import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "groups_team")
public class GroupTeam {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    @NotNull
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "created_at",
            insertable = false,
            updatable = false)
    private LocalDateTime createdAt;

}
