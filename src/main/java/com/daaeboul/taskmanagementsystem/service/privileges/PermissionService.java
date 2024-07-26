package com.daaeboul.taskmanagementsystem.service.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.DuplicatePermissionNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.PermissionNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.repository.privileges.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Creates a new Permission entity. Performs validation on the permission name before saving the entity to the database.
     *
     * @param permission The Permission object containing the data for the new permission.
     * @return The newly created and saved Permission object.
     * @throws RuntimeException (Or a more specific exception) if validation of the permission name fails.
     */
    @Transactional
    public Permission createPermission(Permission permission) {
        validatePermissionName(permission.getPermissionName());
        return permissionRepository.save(permission);
    }

    /**
     * Retrieves a Permission from the database by its ID.
     *
     * @param permissionId The ID of the Permission to retrieve.
     * @return An Optional containing the Permission if found, otherwise an empty Optional.
     */
    public Optional<Permission> findPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId);
    }

    /**
     * Retrieves a Permission from the database by its name (case-insensitive).
     *
     * @param permissionName The name of the Permission to retrieve.
     * @return An Optional containing the Permission if found, otherwise an empty Optional.
     */
    public Optional<Permission> findPermissionByName(String permissionName) {
        return permissionRepository.findByPermissionNameIgnoreCase(permissionName);
    }

    /**
     * Retrieves a list of all Permissions from the database.
     *
     * @return A List of Permission entities.
     */
    public List<Permission> findAllPermissions() {
        return permissionRepository.findAll();
    }

    /**
     * Retrieves a list of all Permissions from the database.
     *
     * @return A List of Permission entities.
     */
    public Page<Permission> findAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    /**
     * Updates an existing Permission with new data.
     *
     * @param updatedPermission The Permission object containing the updated information.
     * @return The updated and saved Permission object.
     * @throws PermissionNotFoundException if a Permission with the provided ID doesn't exist.
     * @throws RuntimeException (Or a more specific exception) if validation of the permission name fails.
     */
    @Transactional
    public Permission updatePermission(Permission updatedPermission) {
        Permission existingPermission = permissionRepository.findById(updatedPermission.getId())
                .orElseThrow(() -> new PermissionNotFoundException(updatedPermission.getId()));

        existingPermission.setPermissionName(updatedPermission.getPermissionName());
        existingPermission.setDescription(updatedPermission.getDescription());
        existingPermission.setRoles(updatedPermission.getRoles());
        existingPermission.setProjectRoles(updatedPermission.getProjectRoles());

        validatePermissionName(existingPermission.getPermissionName());

        return permissionRepository.save(existingPermission);
    }

    /**
     * Deletes a Permission from the database by its ID. Ensures the Permission exists before deletion.
     *
     * @param permissionId The ID of the Permission to delete.
     * @throws PermissionNotFoundException if a Permission with the provided ID doesn't exist.
     */
    @Transactional
    public void deletePermission(Long permissionId) {
        if (!permissionRepository.existsById(permissionId)) {
            throw new PermissionNotFoundException(permissionId);
        }
        permissionRepository.deleteById(permissionId);
    }

    /**
     * Retrieves a set of Permissions associated with a specific Role.
     *
     * @param roleName The name of the Role to retrieve permissions for.
     * @return A Set of Permission entities linked to the specified Role.
     */
    public Set<Permission> findPermissionsByRoleName(String roleName) {
        return permissionRepository.findByRoleName(roleName);
    }

    /**
     * Retrieves a list of Permissions that are not currently assigned to any Roles.
     *
     * @return A List of Permission entities that are currently unused.
     */
    public List<Permission> findUnusedPermissions() {
        return permissionRepository.findUnused();
    }

    /**
     * Helper method used for validating the uniqueness of a Permission name.
     *
     * @param permissionName The Permission name to check for uniqueness.
     * @throws DuplicatePermissionNameException if a Permission with the given name already exists.
     */
    private void validatePermissionName(String permissionName) {
        if (permissionRepository.findByPermissionNameIgnoreCase(permissionName).isPresent()) {
            throw new DuplicatePermissionNameException(permissionName);
        }
    }

}

