package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Retrieves a Permission from the database based on its permission name, ignoring case differences.
     *
     * @param permissionName The name of the permission to search for.
     * @return An Optional containing the Permission if found, otherwise an empty Optional.
     */
    @Query("SELECT p FROM Permission p WHERE LOWER(p.permissionName) = LOWER(:permissionName)")
    Optional<Permission> findByPermissionNameIgnoreCase(@Param("permissionName") String permissionName);

    /**
     * Retrieves a set of Permissions associated with a specific role name.
     *
     * @param roleName The name of the role whose permissions to retrieve.
     * @return A Set of Permissions linked to the specified role.
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.roleName = :roleName")
    Set<Permission> findByRoleName(@Param("roleName") String roleName);

    /**
     * Retrieves a list of Permissions that are not currently assigned to any roles.
     *
     * @return A List of Permissions that are currently unused.
     */
    @Query("SELECT p FROM Permission p WHERE p.roles IS EMPTY")
    List<Permission> findUnused();
}

