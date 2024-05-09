package com.daaeboul.taskmanagementsystem.model.project;

import com.daaeboul.taskmanagementsystem.model.BaseEntity;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "project_role")
public class ProjectRole extends BaseEntity {

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_role_permission",
            joinColumns = @JoinColumn(name = "project_role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions;
}
