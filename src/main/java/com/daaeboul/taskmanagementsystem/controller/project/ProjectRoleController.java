package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectRole.ProjectRoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.service.project.ProjectRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/project-roles")
public class ProjectRoleController {

    private final ProjectRoleService projectRoleService;

    @Autowired
    public ProjectRoleController(ProjectRoleService projectRoleService) {
        this.projectRoleService = projectRoleService;
    }

    @PostMapping
    public ResponseEntity<ProjectRole> createProjectRole(@RequestBody ProjectRole projectRole) {
        ProjectRole createdProjectRole = projectRoleService.createProjectRole(projectRole);
        return ResponseEntity.ok(createdProjectRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectRole> findProjectRoleById(@PathVariable Long id) {
        Optional<ProjectRole> projectRole = projectRoleService.findProjectRoleById(id);
        return projectRole.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProjectRole>> findAllProjectRoles() {
        List<ProjectRole> projectRoles = projectRoleService.findAllProjectRoles();
        return ResponseEntity.ok(projectRoles);
    }

    @GetMapping("/role-name/{roleName}")
    public ResponseEntity<ProjectRole> findProjectRoleByRoleName(@PathVariable String roleName) {
        Optional<ProjectRole> projectRole = projectRoleService.findProjectRoleByRoleName(roleName);
        return projectRole.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<ProjectRole>> findProjectRolesByPermissions(@RequestParam Set<Permission> permissions) {
        List<ProjectRole> projectRoles = projectRoleService.findProjectRolesByPermissions(permissions);
        return ResponseEntity.ok(projectRoles);
    }

    @GetMapping("/permission-name/{permissionName}")
    public ResponseEntity<List<ProjectRole>> findProjectRolesByPermissionName(@PathVariable String permissionName) {
        List<ProjectRole> projectRoles = projectRoleService.findProjectRolesByPermissionName(permissionName);
        return ResponseEntity.ok(projectRoles);
    }

    @GetMapping("/permission-names")
    public ResponseEntity<List<ProjectRole>> findProjectRolesByPermissionNames(@RequestParam Set<String> permissionNames) {
        List<ProjectRole> projectRoles = projectRoleService.findProjectRolesByPermissionNames(permissionNames);
        return ResponseEntity.ok(projectRoles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectRole> updateProjectRole(@PathVariable Long id, @RequestBody ProjectRole projectRoleDetails) {
        try {
            // Fetch the existing project role
            ProjectRole existingProjectRole = projectRoleService.findProjectRoleById(id)
                    .orElseThrow(() -> new ProjectRoleNotFoundException("Project role not found"));

            // Update the existing project role with details from the request
            existingProjectRole.setRoleName(projectRoleDetails.getRoleName());
            existingProjectRole.setDescription(projectRoleDetails.getDescription());
            existingProjectRole.setPermissions(projectRoleDetails.getPermissions());

            // Save the updated project role
            ProjectRole updatedProjectRole = projectRoleService.updateProjectRole(existingProjectRole);
            return ResponseEntity.ok(updatedProjectRole);
        } catch (ProjectRoleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectRole(@PathVariable Long id) {
        try {
            projectRoleService.deleteProjectRole(id);
            return ResponseEntity.noContent().build();
        } catch (ProjectRoleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/role-name/{roleName}")
    public ResponseEntity<Boolean> existsByRoleName(@PathVariable String roleName) {
        boolean exists = projectRoleService.existsByRoleName(roleName);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/at-least-one-permission")
    public ResponseEntity<List<ProjectRole>> findProjectRolesWithAtLeastOnePermission() {
        List<ProjectRole> projectRoles = projectRoleService.findProjectRolesWithAtLeastOnePermission();
        return ResponseEntity.ok(projectRoles);
    }
}