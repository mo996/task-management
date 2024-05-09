package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectRole.ProjectRoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProjectRoleServiceTest {

    @Autowired
    private ProjectRoleService projectRoleService;

    @MockBean
    private ProjectRoleRepository projectRoleRepository;

    @Test
    void shouldCreateProjectRole() {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setRoleName("Developer");

        when(projectRoleRepository.save(any(ProjectRole.class))).thenReturn(projectRole);

        ProjectRole createdProjectRole = projectRoleService.createProjectRole(projectRole);

        assertNotNull(createdProjectRole);
        assertEquals("Developer", createdProjectRole.getRoleName());
        verify(projectRoleRepository).save(projectRole);
    }

    @Test
    void shouldFindProjectRoleById() {
        Long id = 1L;
        ProjectRole projectRole = new ProjectRole();
        ReflectionTestUtils.setField(projectRole, "id", id);
        projectRole.setRoleName("Developer");

        when(projectRoleRepository.findById(id)).thenReturn(Optional.of(projectRole));

        Optional<ProjectRole> foundProjectRole = projectRoleService.findProjectRoleById(id);

        assertTrue(foundProjectRole.isPresent());
        assertEquals(projectRole, foundProjectRole.get());
        verify(projectRoleRepository).findById(id);
    }

    @Test
    void shouldFindAllProjectRoles() {
        List<ProjectRole> projectRoles = Arrays.asList(new ProjectRole(), new ProjectRole());

        when(projectRoleRepository.findAll()).thenReturn(projectRoles);

        List<ProjectRole> foundProjectRoles = projectRoleService.findAllProjectRoles();

        assertNotNull(foundProjectRoles);
        assertEquals(2, foundProjectRoles.size());
        verify(projectRoleRepository).findAll();
    }

    @Test
    void shouldFindProjectRoleByRoleName() {
        String roleName = "Developer";
        ProjectRole projectRole = new ProjectRole();
        projectRole.setRoleName(roleName);

        when(projectRoleRepository.findByRoleName(roleName)).thenReturn(Optional.of(projectRole));

        Optional<ProjectRole> foundProjectRole = projectRoleService.findProjectRoleByRoleName(roleName);

        assertTrue(foundProjectRole.isPresent());
        assertEquals(projectRole, foundProjectRole.get());
        verify(projectRoleRepository).findByRoleName(roleName);
    }

    @Test
    void shouldFindProjectRolesByPermissions() {
        Permission permission1 = new Permission("permission1");
        Permission permission2 = new Permission("permission2");
        ProjectRole projectRole1 = new ProjectRole();
        projectRole1.setPermissions(new HashSet<>(List.of(permission1)));
        ProjectRole projectRole2 = new ProjectRole();
        projectRole2.setPermissions(new HashSet<>(Arrays.asList(permission1, permission2)));

        when(projectRoleRepository.findByPermissionsIn(anySet())).thenReturn(Arrays.asList(projectRole1, projectRole2));

        List<ProjectRole> foundProjectRoles = projectRoleService.findProjectRolesByPermissions(new HashSet<>(List.of(permission1)));

        assertNotNull(foundProjectRoles);
        assertEquals(2, foundProjectRoles.size());
        verify(projectRoleRepository).findByPermissionsIn(anySet());
    }

    @Test
    void shouldFindProjectRolesByPermissionName() {
        String permissionName = "permission1";
        ProjectRole projectRole = new ProjectRole();
        projectRole.setPermissions(new HashSet<>(List.of(new Permission(permissionName))));

        when(projectRoleRepository.findByPermissionName(permissionName)).thenReturn(List.of(projectRole));

        List<ProjectRole> foundProjectRoles = projectRoleService.findProjectRolesByPermissionName(permissionName);

        assertNotNull(foundProjectRoles);
        assertEquals(1, foundProjectRoles.size());
        assertEquals(projectRole, foundProjectRoles.get(0));
        verify(projectRoleRepository).findByPermissionName(permissionName);
    }

    @Test
    void shouldFindProjectRolesByPermissionNames() {
        String permissionName1 = "permission1";
        String permissionName2 = "permission2";
        ProjectRole projectRole = new ProjectRole();
        projectRole.setPermissions(new HashSet<>(Arrays.asList(new Permission(permissionName1), new Permission(permissionName2))));

        when(projectRoleRepository.findByPermissionNames(anySet(), anyInt())).thenReturn(List.of(projectRole));

        List<ProjectRole> foundProjectRoles = projectRoleService.findProjectRolesByPermissionNames(new HashSet<>(Arrays.asList(permissionName1, permissionName2)));

        assertNotNull(foundProjectRoles);
        assertEquals(1, foundProjectRoles.size());
        assertEquals(projectRole, foundProjectRoles.get(0));
        verify(projectRoleRepository).findByPermissionNames(anySet(), anyInt());
    }

    @Test
    void shouldUpdateProjectRole() {
        Long id = 1L;
        ProjectRole existingProjectRole = new ProjectRole();
        ReflectionTestUtils.setField(existingProjectRole, "id", id);
        existingProjectRole.setRoleName("Developer");

        ProjectRole updatedProjectRole = new ProjectRole();
        ReflectionTestUtils.setField(updatedProjectRole, "id", id);
        updatedProjectRole.setRoleName("Senior Developer");

        when(projectRoleRepository.findById(id)).thenReturn(Optional.of(existingProjectRole));
        when(projectRoleRepository.save(any(ProjectRole.class))).thenReturn(updatedProjectRole);

        ProjectRole result = projectRoleService.updateProjectRole(updatedProjectRole);

        assertNotNull(result);
        assertEquals("Senior Developer", result.getRoleName());
        verify(projectRoleRepository).save(any(ProjectRole.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProjectRole() {
        Long id = 1L;
        ProjectRole updatedProjectRole = new ProjectRole();
        ReflectionTestUtils.setField(updatedProjectRole, "id", id);

        when(projectRoleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectRoleNotFoundException.class, () -> projectRoleService.updateProjectRole(updatedProjectRole));
    }

    @Test
    void shouldDeleteProjectRoleById() {
        Long id = 1L;

        when(projectRoleRepository.existsById(id)).thenReturn(true);
        doNothing().when(projectRoleRepository).deleteById(id);

        projectRoleService.deleteProjectRole(id);

        verify(projectRoleRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProjectRole() {
        Long id = 1L;

        when(projectRoleRepository.existsById(id)).thenReturn(false);

        assertThrows(ProjectRoleNotFoundException.class, () -> projectRoleService.deleteProjectRole(id));
    }

    @Test
    void shouldCheckIfProjectRoleExistsByRoleName() {
        String roleName = "Developer";

        when(projectRoleRepository.existsByRoleName(roleName)).thenReturn(true);

        boolean exists = projectRoleService.existsByRoleName(roleName);

        assertTrue(exists);
        verify(projectRoleRepository).existsByRoleName(roleName);
    }

    @Test
    void shouldFindProjectRolesWithAtLeastOnePermission() {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setPermissions(new HashSet<>(List.of(new Permission("permission1"))));

        when(projectRoleRepository.findWithAtLeastOnePermission()).thenReturn(List.of(projectRole));

        List<ProjectRole> foundProjectRoles = projectRoleService.findProjectRolesWithAtLeastOnePermission();

        assertNotNull(foundProjectRoles);
        assertEquals(1, foundProjectRoles.size());
        assertEquals(projectRole, foundProjectRoles.get(0));
        verify(projectRoleRepository).findWithAtLeastOnePermission();
    }
}
