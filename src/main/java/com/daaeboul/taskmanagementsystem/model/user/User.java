package com.daaeboul.taskmanagementsystem.model.user;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.SoftDeletable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;


import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "user")
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE id = ?")
public class User extends BaseEntity implements SoftDeletable {

    @Size(min = 8)
    @NotBlank
    @NonNull
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    )
    private Set<Group> groups = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserDetails userDetails;

    @Override
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
