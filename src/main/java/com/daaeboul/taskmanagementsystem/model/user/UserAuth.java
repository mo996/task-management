package com.daaeboul.taskmanagementsystem.model.user;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_auth")
@SQLDelete(sql = "UPDATE user_auth SET deleted_at = NOW() WHERE id = ?")
public class UserAuth extends BaseEntity implements SoftDeletable {


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @NotBlank
    @Size(max = 60)
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Size(max = 255)
    @Column(name = "auth_token", length = 255)
    private String authToken;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    @Column(name = "is_locked")
    private boolean isLocked;

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
