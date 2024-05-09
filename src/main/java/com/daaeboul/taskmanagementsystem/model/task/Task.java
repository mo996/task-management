package com.daaeboul.taskmanagementsystem.model.task;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "task")
@SQLDelete(sql = "UPDATE task SET deleted_at = NOW() WHERE id = ?")
public class Task extends BaseEntity implements SoftDeletable {

    @NotBlank
    @Column(name = "task_title", nullable = false)
    private String taskTitle;

    @Column(name = "task_description")
    private String taskDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(name = "task_due_date")
    private LocalDate taskDueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id")
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Implementing SoftDeletable
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Override
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Tasks that depend on THIS task
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskDependency> dependencies = new HashSet<>();

    // Tasks that THIS task depends on
    @OneToMany(mappedBy = "dependsOnTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskDependency> precedencies = new HashSet<>();
}
