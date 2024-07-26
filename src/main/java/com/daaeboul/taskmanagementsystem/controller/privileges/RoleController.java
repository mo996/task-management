package com.daaeboul.taskmanagementsystem.controller.privileges;


import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.RoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import com.daaeboul.taskmanagementsystem.service.privileges.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {


    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        Optional<Role> role = roleService.findRoleById(roleId);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        Optional<Role> role = roleService.findRoleByName(roleName);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestBody Role roleDetails) {
        try {
            Role updatedRole = roleService.updateRole(roleId, roleDetails);
            return ResponseEntity.ok(updatedRole);
        } catch (RoleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.noContent().build();
        } catch (RoleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}