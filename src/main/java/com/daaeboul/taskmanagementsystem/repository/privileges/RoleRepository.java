package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Retrieves a Role from the database based on its unique role name.
     *
     * @param roleName The name of the role to search for.
     * @return An Optional containing the Role if found, otherwise an empty Optional.
     */
    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    Optional<Role> findByRoleName(String roleName);

    /**
     *
     * Retrieves a list of Roles that possess at least one of the provided permission names.
     *
     * @param permissionNames A List of permission names to search for.
     * @return A List of Roles containing at least one of the specified permissions.
     */
    List<Role> findByPermissionsPermissionNameIn(List<String> permissionNames);

    /**
     * Checks if a Role exists with the specified role name and has a Permission with the specified permission name associated with it.
     *
     * @param roleName The name of the role to check.
     * @param permissionName The name of the permission to check.
     * @return True if the Role exists and has the associated Permission, otherwise false.
     */
    boolean existsByRoleNameAndPermissionsPermissionName(String roleName, String permissionName);

    /**
     * Retrieves a list of Roles that are not currently assigned to any users. This method uses a subquery to identify unassigned roles.
     *
     * @return A List of Roles that are not assigned to any users.
     */
    @Query("SELECT r FROM Role r WHERE r NOT IN (SELECT DISTINCT g.role FROM Group g JOIN g.users u)")
    List<Role> findUnassignedRoles();

    /**
     * Checks if a Role with the specified role name exists in the database.
     *
     * @param roleName The name of the role to check for existence.
     * @return True if a Role with the given name exists, otherwise false.
     */
    boolean existsByRoleName(String roleName);
}
