package com.daaeboul.taskmanagementsystem.controller.privileges;


import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.PermissionNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.service.privileges.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long permissionId) {
        Optional<Permission> permission = permissionService.findPermissionById(permissionId);
        return permission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{permissionName}")
    public ResponseEntity<Permission> getPermissionByName(@PathVariable String permissionName) {
        Optional<Permission> permission = permissionService.findPermissionByName(permissionName);
        return permission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.findAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long permissionId, @RequestBody Permission permissionDetails) {
        try {
            // Retrieve the existing permission and update its details
            Permission existingPermission = permissionService.findPermissionById(permissionId)
                    .orElseThrow(() -> new PermissionNotFoundException(permissionId));

            existingPermission.setPermissionName(permissionDetails.getPermissionName());
            existingPermission.setDescription(permissionDetails.getDescription());
            existingPermission.setRoles(permissionDetails.getRoles());
            existingPermission.setProjectRoles(permissionDetails.getProjectRoles());

            // Update the permission
            Permission updatedPermission = permissionService.updatePermission(existingPermission);
            return ResponseEntity.ok(updatedPermission);
        } catch (PermissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long permissionId) {
        try {
            permissionService.deletePermission(permissionId);
            return ResponseEntity.noContent().build();
        } catch (PermissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}