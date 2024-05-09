package com.daaeboul.taskmanagementsystem.service.privileges;



import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.DuplicateRoleNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.RoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import com.daaeboul.taskmanagementsystem.repository.privileges.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves a Role from the database by its name.
     *
     * @param roleName The name of the Role to retrieve.
     * @return An Optional containing the Role if found, otherwise an empty Optional.
     */
    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    /**
     * Retrieves a list of Roles possessing at least one of the specified Permission names.
     *
     * @param permissionNames A List of Permission names to filter Roles by.
     * @return A List of Roles containing at least one of the specified Permissions.
     */
    public List<Role> findRolesByPermissionNames(List<String> permissionNames) {
        return roleRepository.findByPermissionsPermissionNameIn(permissionNames);
    }

    /**
     * Checks if a Role with the given name possesses a Permission with the specified permission name.
     *
     * @param roleName The name of the Role to check.
     * @param permissionName The name of the Permission to check for.
     * @return True if the Role exists and possesses the specified Permission; otherwise, false.
     */
    public boolean checkRoleHasPermission(String roleName, String permissionName) {
        return roleRepository.existsByRoleNameAndPermissionsPermissionName(roleName, permissionName);
    }

    /**
     * Retrieves a list of Roles that are not currently assigned to any users. This method likely relies on a custom query within your `RoleRepository`.
     *
     * @return A List of Roles that are not assigned to any users.
     */
    public List<Role> findUnassignedRoles() {
        return roleRepository.findUnassignedRoles();
    }

    /**
     * Creates a new Role entity. Performs validation on the role name before saving the entity to the database.
     *
     * @param role The Role objectcontaining the data for the new role.
     * @return The newly created and saved Role object.
     * @throws RuntimeException (Or a more specific exception) if validation of the role name fails.
     */
    @Transactional
    public Role createRole(Role role) {
        validateRoleName(role.getRoleName());
        return roleRepository.save(role);
    }

    /**
     * Updates a Role with the provided data.
     *
     * @param roleId The ID of the Role to update.
     * @param roleDetails The Role object containing the updated data.
     * @return The updated and saved Role object.
     * @throws RoleNotFoundException if a Role with the provided ID doesn't exist.
     */
    @Transactional
    public Role updateRole(Long roleId, Role roleDetails) {
        // Check if the role exists before updating
        Role roleToUpdate = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));

        roleToUpdate.setRoleName(roleDetails.getRoleName());
        roleToUpdate.setDescription(roleDetails.getDescription());

        return roleRepository.save(roleToUpdate);
    }

    /**
     * Deletes a Role from the database by its ID.
     *
     * @param roleId The ID of the Role to delete.
     * @throws RoleNotFoundException if a Role with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }


    /**
     * Helper method for validating the uniqueness of a Role name. You could include additional validation criteria here as needed.
     *
     * @param roleName The Role name to check for uniqueness.
     * @throws DuplicateRoleNameException if a Role with the given name already exists.
     */
    private void validateRoleName(String roleName) {
       if (roleRepository.existsByRoleName(roleName)){
           throw new DuplicateRoleNameException(roleName);
       }
    }
}

