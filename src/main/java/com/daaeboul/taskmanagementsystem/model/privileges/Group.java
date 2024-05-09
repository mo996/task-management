package com.daaeboul.taskmanagementsystem.model.privileges;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import com.daaeboul.taskmanagementsystem.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "`group`") // Backticks are used because "group" is a reserved keyword in SQL
@SQLDelete(sql = "UPDATE `group` SET deleted_at = NOW() WHERE id = ?")
public class Group extends BaseEntity implements SoftDeletable {


    @NotBlank
    @NonNull
    @Column(name = "group_name", nullable = false, unique = true, length = 100)
    private String groupName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

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
