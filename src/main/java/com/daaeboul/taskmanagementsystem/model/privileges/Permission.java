package com.daaeboul.taskmanagementsystem.model.privileges;


import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "permission")
public class Permission extends BaseEntity {

    @NotBlank
    @NonNull
    @Column(name = "permission_name", nullable = false, unique = true, length = 100)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<ProjectRole> projectRoles = new HashSet<>();
}
