package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectRole.ProjectRoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectRoleService {

    private final ProjectRoleRepository projectRoleRepository;

    @Autowired
    public ProjectRoleService(ProjectRoleRepository projectRoleRepository) {
        this.projectRoleRepository = projectRoleRepository;
    }

    /**
     * Creates a new project role.
     *
     * @param projectRole The ProjectRole entity to create.
     * @return The saved ProjectRole entity.
     */
    @Transactional
    public ProjectRole createProjectRole(ProjectRole projectRole) {
        return projectRoleRepository.save(projectRole);
    }

    /**
     * Finds a project role by its ID.
     *
     * @param id The ID of the project role.
     * @return An Optional containing the ProjectRole entity if found, otherwise empty.
     */
    public Optional<ProjectRole> findProjectRoleById(Long id) {
        return projectRoleRepository.findById(id);
    }

    /**
     * Finds all project roles.
     *
     * @return A list of all ProjectRole entities.
     */
    public List<ProjectRole> findAllProjectRoles() {
        return projectRoleRepository.findAll();
    }

    /**
     * Finds a project role by its role name.
     *
     * @param roleName The name of the project role.
     * @return An Optional containing the ProjectRole entity if found, otherwise empty.
     */
    public Optional<ProjectRole> findProjectRoleByRoleName(String roleName) {
        return projectRoleRepository.findByRoleName(roleName);
    }

    /**
     * Finds project roles by a set of permissions.
     *
     * @param permissions The set of permissions to search for.
     * @return A list of ProjectRole entities that have any of the specified permissions.
     */
    public List<ProjectRole> findProjectRolesByPermissions(Set<Permission> permissions) {
        return projectRoleRepository.findByPermissionsIn(permissions);
    }

    /**
     * Finds project roles by permission name.
     *
     * @param permissionName The name of the permission to search for.
     * @return A list of ProjectRole entities associated with the specified permission.
     */
    public List<ProjectRole> findProjectRolesByPermissionName(String permissionName) {
        return projectRoleRepository.findByPermissionName(permissionName);
    }

    /**
     * Finds project roles that have all of the specified permissions.
     *
     * @param permissionNames The set of permission names to search for.
     * @return A list of ProjectRole entities that have all of the specified permissions.
     */
    public List<ProjectRole> findProjectRolesByPermissionNames(Set<String> permissionNames) {
        return projectRoleRepository.findByPermissionNames(permissionNames, permissionNames.size());
    }

    /**
     * Updates a project role.
     *
     * @param updatedProjectRole The ProjectRole entity with updated information.
     * @return The updated ProjectRole entity.
     * @throws ProjectRoleNotFoundException If the project role is not found.
     */
    @Transactional
    public ProjectRole updateProjectRole(ProjectRole updatedProjectRole) {
        ProjectRole existingProjectRole = projectRoleRepository.findById(updatedProjectRole.getId())
                .orElseThrow(() -> new ProjectRoleNotFoundException("Project role not found"));

        existingProjectRole.setRoleName(updatedProjectRole.getRoleName());
        existingProjectRole.setPermissions(updatedProjectRole.getPermissions());
        existingProjectRole.setDescription(updatedProjectRole.getDescription());

        return projectRoleRepository.save(existingProjectRole);
    }

    /**
     * Deletes a project role by its ID.
     *
     * @param id The ID of the project role to delete.
     * @throws ProjectRoleNotFoundException If the project role is not found.
     */
    @Transactional
    public void deleteProjectRole(Long id) {
        if (!projectRoleRepository.existsById(id)) {
            throw new ProjectRoleNotFoundException("Project role not found");
        }
        projectRoleRepository.deleteById(id);
    }

    /**
     * Checks if a project role with the specified name exists.
     *
     * @param roleName The name of the project role to check.
     * @return true if a project role with the given name exists, false otherwise.
     */
    public boolean existsByRoleName(String roleName) {
        return projectRoleRepository.existsByRoleName(roleName);
    }

    /**
     * Finds all project roles that have at least one permission associated with them.
     * This is useful for ensuring that roles have meaningful access control within projects.
     *
     * @return A list of ProjectRole objects that have at least one permission.
     */
    public List<ProjectRole> findProjectRolesWithAtLeastOnePermission() {
        return projectRoleRepository.findWithAtLeastOnePermission();
    }
}
