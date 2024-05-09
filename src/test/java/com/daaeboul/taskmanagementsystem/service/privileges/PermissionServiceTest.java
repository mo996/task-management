package com.daaeboul.taskmanagementsystem.service.privileges;


import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.DuplicatePermissionNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.PermissionNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.repository.privileges.PermissionRepository;
import com.daaeboul.taskmanagementsystem.service.privileges.PermissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;

    @MockBean
    private PermissionRepository permissionRepository;

    @Test
    void shouldCreatePermission() {
        Permission newPermission = new Permission();
        newPermission.setPermissionName("READ_ALL");
        when(permissionRepository.save(newPermission)).thenReturn(newPermission);

        Permission createdPermission = permissionService.createPermission(newPermission);

        Assertions.assertNotNull(createdPermission);
        assertEquals("READ_ALL", createdPermission.getPermissionName());
        verify(permissionRepository).save(newPermission);
    }

    @Test
    void shouldThrowExceptionWhenPermissionNameIsDuplicate() {
        Permission duplicatePermission = new Permission();
        duplicatePermission.setPermissionName("WRITE_ALL");
        when(permissionRepository.findByPermissionNameIgnoreCase("WRITE_ALL")).thenReturn(Optional.of(duplicatePermission));

        Exception exception = assertThrows(DuplicatePermissionNameException.class, () -> permissionService.createPermission(duplicatePermission));

        assertEquals("A permission with the name 'WRITE_ALL' already exists.", exception.getMessage());
    }

    @Test
    void shouldFindPermissionById() {
        Long permissionId = 1L;
        Permission expectedPermission = new Permission();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(expectedPermission));

        Optional<Permission> foundPermission = permissionService.findPermissionById(permissionId);

        assertTrue(foundPermission.isPresent(), "Permission should be found");
        assertEquals(expectedPermission, foundPermission.get(), "Found Permission does not match the expected");
    }


    @Test
    void shouldReturnEmptyOptionalWhenPermissionNotFoundById() {
        Long permissionId = 999L;
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        Optional<Permission> foundPermission = permissionService.findPermissionById(permissionId);

        assertTrue(foundPermission.isEmpty());
    }

    @Test
    void shouldFindAllPermissions() {
        List<Permission> expectedPermissions = Arrays.asList(new Permission(), new Permission());
        when(permissionRepository.findAll()).thenReturn(expectedPermissions);

        List<Permission> foundPermissions = permissionService.findAllPermissions();

        assertEquals(expectedPermissions, foundPermissions);
    }

    @Test
    void shouldFindAllPermissionsWithPagination() {
        Page<Permission> expectedPermissions = new PageImpl<>(Arrays.asList(new Permission(), new Permission()));
        PageRequest pageable = PageRequest.of(0, 2);
        when(permissionRepository.findAll(pageable)).thenReturn(expectedPermissions);

        Page<Permission> foundPermissions = permissionService.findAllPermissions(pageable);

        assertEquals(expectedPermissions.getTotalElements(), foundPermissions.getTotalElements());
    }

    @Test
    void shouldUpdatePermissionSuccessfully() {
        Long permissionId = 1L;
        Permission existingPermission = new Permission();

        // Simulate setting ID without a setter
        ReflectionTestUtils.setField(existingPermission, "id", permissionId);
        existingPermission.setPermissionName("READ_ONLY");
        existingPermission.setDescription("Can only read data");

        Permission updatedPermission = new Permission();
        ReflectionTestUtils.setField(updatedPermission, "id", permissionId);
        updatedPermission.setPermissionName("READ_ALL");
        updatedPermission.setDescription("Can read all data");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(updatedPermission);

        Permission result = permissionService.updatePermission(updatedPermission);

        assertNotNull(result);
        assertEquals(permissionId, ReflectionTestUtils.getField(result, "id"));
        assertEquals("READ_ALL", result.getPermissionName());
        assertEquals("Can read all data", result.getDescription());
        verify(permissionRepository).save(any(Permission.class));
    }


    @Test
    void shouldThrowPermissionNotFoundExceptionOnUpdateWhenPermissionDoesNotExist() {
        Long nonExistingPermissionId = 999L;
        Permission nonExistingPermission = new Permission();

        nonExistingPermission.setPermissionName("NON_EXISTENT_PERMISSION");

        when(permissionRepository.findById(nonExistingPermissionId)).thenReturn(Optional.empty());

        Permission updateAttempt = new Permission();
        updateAttempt.setPermissionName("UPDATED_NAME");

        assertThrows(PermissionNotFoundException.class, () -> permissionService.updatePermission(nonExistingPermission));
    }



    @Test
    void shouldDeletePermissionSuccessfully() {
        Long permissionId = 1L;
        when(permissionRepository.existsById(permissionId)).thenReturn(true);

        permissionService.deletePermission(permissionId);

        verify(permissionRepository).deleteById(permissionId);
    }

    @Test
    void shouldThrowPermissionNotFoundExceptionOnDeleteWhenPermissionDoesNotExist() {
        Long permissionId = 999L;
        when(permissionRepository.existsById(permissionId)).thenReturn(false);

        assertThrows(PermissionNotFoundException.class, () -> permissionService.deletePermission(permissionId));
    }

    @Test
    void shouldFindPermissionsByRoleName() {
        String roleName = "admin";
        Set<Permission> expectedPermissions = Set.of(new Permission());

        when(permissionRepository.findByRoleName(roleName)).thenReturn(expectedPermissions);

        Set<Permission> result = permissionService.findPermissionsByRoleName(roleName);

        assertEquals(expectedPermissions, result);
        verify(permissionRepository).findByRoleName(roleName);
    }

    @Test
    void shouldFindUnusedPermissions() {
        List<Permission> expectedPermissions = List.of(new Permission());

        when(permissionRepository.findUnused()).thenReturn(expectedPermissions);

        List<Permission> result = permissionService.findUnusedPermissions();

        assertEquals(expectedPermissions, result);
        verify(permissionRepository).findUnused();
    }

}
