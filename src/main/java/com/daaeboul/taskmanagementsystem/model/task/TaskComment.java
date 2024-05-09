package com.daaeboul.taskmanagementsystem.model.task;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import com.daaeboul.taskmanagementsystem.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "task_comment")
@SQLDelete(sql = "UPDATE task_comment SET deleted_at = NOW() WHERE id = ?")
public class TaskComment extends BaseEntity implements SoftDeletable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "comment", nullable = false)
    private String comment;

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
}
