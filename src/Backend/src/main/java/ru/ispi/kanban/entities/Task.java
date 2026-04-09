package ru.ispi.kanban.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.ispi.kanban.enums.TaskPriority;
import ru.ispi.kanban.enums.TaskStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "column_id", nullable = false)
    private BoardColumn column;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(nullable = false)
    private Long position;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private Task copy(Task task) {
        Task copy = new Task();
        copy.setTitle(task.getTitle());
        copy.setDescription(task.getDescription());
        copy.setColumn(task.getColumn());
        copy.setAssignee(task.getAssignee());
        copy.setPosition(task.getPosition());
        copy.setDeadline(task.getDeadline());
        copy.setPriority(task.getPriority());
        copy.setStatus(task.getStatus());
        return copy;
    }
}