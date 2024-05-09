package com.daaeboul.taskmanagementsystem.model.privileges;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "role")
public class Role extends BaseEntity {

    @NotBlank
    @NonNull
    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions = new HashSet<>();
}