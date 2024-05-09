package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Long> {

    /**
     * Retrieves a ProjectRole from the database based on its unique role name.
     *
     * @param roleName The name of the ProjectRole to search for.
     * @return An Optional containing the ProjectRole if found, otherwise an empty Optional.
     */
    Optional<ProjectRole> findByRoleName(String roleName);

    /**
     * Retrieves a list of ProjectRoles that contain at least one of the Permission entities from the provided set.
     *
     * @param permissions A Set of Permissions to filter ProjectRoles by.
     * @return A List of ProjectRoles containing at least one of the specified Permissions.
     */
    List<ProjectRole> findByPermissionsIn(Set<Permission> permissions);

    /**
     * Retrieves a list of ProjectRoles that are associated with a Permission having the specified permission name.
     * This method uses a JOIN query for efficiency.
     *
     * @param permissionName The name of the Permission to search within ProjectRoles.
     * @return A List of ProjectRoles containing the Permission with the specified name.
     */
    @Query("SELECT pr FROM ProjectRole pr JOIN pr.permissions p WHERE p.permissionName = :permissionName")
    List<ProjectRole> findByPermissionName(String permissionName);

    /**
     * Retrieves a list of ProjectRoles that have exactly the set of Permissions specified by their names.
     * The query ensures that the number of associated permissions matches the size of the provided permissionNames set.
     *
     * @param permissionNames A Set of Permission names.
     * @param size The expected number of Permissions for matching ProjectRoles.
     * @return A List of ProjectRoles matching the criteria.
     */
    @Query("SELECT pr FROM ProjectRole pr JOIN pr.permissions p WHERE p.permissionName IN :permissionNames GROUP BY pr HAVING COUNT(DISTINCT p.id) = :size")
    List<ProjectRole> findByPermissionNames(Set<String> permissionNames, int size);

    /**
     * Checks if a ProjectRole with the specified role name exists in the database.
     *
     * @param roleName The name of the role to check for existence.
     * @return True if a ProjectRole with the given name exists, otherwise false.
     */
    boolean existsByRoleName(String roleName);

    /**
     * Retrieves a list of ProjectRoles that have at least one associated Permission.
     *
     * @return A List of ProjectRoles possessing one or more Permissions.
     */
    @Query("SELECT pr FROM ProjectRole pr JOIN pr.permissions p WHERE p.id IS NOT NULL")
    List<ProjectRole> findWithAtLeastOnePermission();
}
