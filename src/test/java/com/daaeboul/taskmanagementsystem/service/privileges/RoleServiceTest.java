package com.daaeboul.taskmanagementsystem.service.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.DuplicateRoleNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.RoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import com.daaeboul.taskmanagementsystem.repository.privileges.RoleRepository;
import com.daaeboul.taskmanagementsystem.service.privileges.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    void findRoleByNameShouldReturnRole() {
        String roleName = "testAdmin";
        Role role = new Role();
        role.setRoleName(roleName);
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));

        Optional<Role> foundRole = roleService.findRoleByName(roleName);

        assertTrue(foundRole.isPresent());
        assertEquals(roleName, foundRole.get().getRoleName());
        verify(roleRepository).findByRoleName(roleName);
    }

    @Test
    void createRoleShouldPersistRole() {
        Role role = new Role();
        role.setRoleName("testUser");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role createdRole = roleService.createRole(role);

        assertNotNull(createdRole);
        assertEquals("testUser", createdRole.getRoleName());
        verify(roleRepository).save(role);
    }

    @Test
    void createRoleWithDuplicateNameShouldThrowException() {
        String roleName = "testUser";
        when(roleRepository.existsByRoleName(roleName)).thenReturn(true);

        Role role = new Role();
        role.setRoleName(roleName);
        assertThrows(DuplicateRoleNameException.class, () -> roleService.createRole(role));

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void updateRoleShouldChangeDetails() {
        Long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setRoleName("testAdmin");
        existingRole.setDescription("Existing description");

        Role newDetails = new Role();
        newDetails.setRoleName("UpdatedName");
        newDetails.setDescription("Updated description");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(newDetails);

        Role updatedRole = roleService.updateRole(roleId, newDetails);

        assertNotNull(updatedRole);
        assertEquals("UpdatedName", updatedRole.getRoleName());
        assertEquals("Updated description", updatedRole.getDescription());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void updateNonExistentRoleShouldThrowException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.updateRole(99L, new Role()));
    }

    @Test
    void deleteRoleShouldUseRepository() {
        doNothing().when(roleRepository).deleteById(anyLong());

        roleService.deleteRole(1L);

        verify(roleRepository).deleteById(anyLong());
    }

    @Test
    void findRolesByPermissionNamesShouldReturnRoles() {
        List<String> permissionNames = Arrays.asList("READ", "WRITE");
        List<Role> expectedRoles = Arrays.asList(new Role(), new Role());
        when(roleRepository.findByPermissionsPermissionNameIn(permissionNames)).thenReturn(expectedRoles);

        List<Role> roles = roleService.findRolesByPermissionNames(permissionNames);

        assertEquals(expectedRoles.size(), roles.size());
        verify(roleRepository).findByPermissionsPermissionNameIn(permissionNames);
    }

    @Test
    void checkRoleHasPermissionShouldReturnTrue() {
        when(roleRepository.existsByRoleNameAndPermissionsPermissionName("testAdmin", "READ")).thenReturn(true);

        boolean result = roleService.checkRoleHasPermission("testAdmin", "READ");

        assertTrue(result);
        verify(roleRepository).existsByRoleNameAndPermissionsPermissionName("testAdmin", "READ");
    }

    @Test
    void findUnassignedRolesShouldReturnRoles() {
        List<Role> expectedRoles = Arrays.asList(new Role(), new Role());
        when(roleRepository.findUnassignedRoles()).thenReturn(expectedRoles);

        List<Role> unassignedRoles = roleService.findUnassignedRoles();

        assertEquals(expectedRoles.size(), unassignedRoles.size());
        verify(roleRepository).findUnassignedRoles();
    }
}
