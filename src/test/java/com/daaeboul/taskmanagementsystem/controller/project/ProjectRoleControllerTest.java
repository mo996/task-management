package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectRole.ProjectRoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.service.project.ProjectRoleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectRoleController.class)
public class ProjectRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectRoleService projectRoleService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateProjectRole() throws Exception {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setRoleName("Role A");

        Mockito.when(projectRoleService.createProjectRole(any(ProjectRole.class))).thenReturn(projectRole);

        mockMvc.perform(post("/api/v1/project-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\": \"Role A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Role A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRoleById() throws Exception {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setRoleName("Role A");

        Mockito.when(projectRoleService.findProjectRoleById(anyLong())).thenReturn(Optional.of(projectRole));

        mockMvc.perform(get("/api/v1/project-roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Role A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRoleById_NotFound() throws Exception {
        Mockito.when(projectRoleService.findProjectRoleById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/project-roles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllProjectRoles() throws Exception {
        ProjectRole projectRole1 = new ProjectRole();
        projectRole1.setRoleName("Role A");

        ProjectRole projectRole2 = new ProjectRole();
        projectRole2.setRoleName("Role B");

        List<ProjectRole> projectRoles = Arrays.asList(projectRole1, projectRole2);

        Mockito.when(projectRoleService.findAllProjectRoles()).thenReturn(projectRoles);

        mockMvc.perform(get("/api/v1/project-roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleName").value("Role A"))
                .andExpect(jsonPath("$[1].roleName").value("Role B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRoleByRoleName() throws Exception {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setRoleName("Role A");

        Mockito.when(projectRoleService.findProjectRoleByRoleName(anyString())).thenReturn(Optional.of(projectRole));

        mockMvc.perform(get("/api/v1/project-roles/role-name/Role A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Role A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRoleByRoleName_NotFound() throws Exception {
        Mockito.when(projectRoleService.findProjectRoleByRoleName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/project-roles/role-name/Role A"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRolesByPermissions() throws Exception {
        ProjectRole projectRole1 = new ProjectRole();
        ProjectRole projectRole2 = new ProjectRole();
        List<ProjectRole> projectRoles = Arrays.asList(projectRole1, projectRole2);

        Mockito.when(projectRoleService.findProjectRolesByPermissions(anySet())).thenReturn(projectRoles);

        mockMvc.perform(get("/api/v1/project-roles/permissions")
                        .param("permissions", "permission1", "permission2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRolesByPermissionName() throws Exception {
        ProjectRole projectRole1 = new ProjectRole();
        ProjectRole projectRole2 = new ProjectRole();
        List<ProjectRole> projectRoles = Arrays.asList(projectRole1, projectRole2);

        Mockito.when(projectRoleService.findProjectRolesByPermissionName(anyString())).thenReturn(projectRoles);

        mockMvc.perform(get("/api/v1/project-roles/permission-name/Permission1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRolesByPermissionNames() throws Exception {
        ProjectRole projectRole1 = new ProjectRole();
        ProjectRole projectRole2 = new ProjectRole();
        List<ProjectRole> projectRoles = Arrays.asList(projectRole1, projectRole2);

        Mockito.when(projectRoleService.findProjectRolesByPermissionNames(anySet())).thenReturn(projectRoles);

        mockMvc.perform(get("/api/v1/project-roles/permission-names")
                        .param("permissionNames", "Permission1", "Permission2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProjectRole() throws Exception {
        ProjectRole existingProjectRole = new ProjectRole();
        existingProjectRole.setRoleName("Existing Role");

        ProjectRole updatedProjectRole = new ProjectRole();
        updatedProjectRole.setRoleName("Role A");

        Mockito.when(projectRoleService.findProjectRoleById(anyLong())).thenReturn(Optional.of(existingProjectRole));
        Mockito.when(projectRoleService.updateProjectRole(any(ProjectRole.class))).thenReturn(updatedProjectRole);

        mockMvc.perform(put("/api/v1/project-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\": \"Role A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Role A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectRole() throws Exception {
        Mockito.doNothing().when(projectRoleService).deleteProjectRole(anyLong());

        mockMvc.perform(delete("/api/v1/project-roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectRole_NotFound() throws Exception {
        Mockito.doThrow(new ProjectRoleNotFoundException("Project role not found")).when(projectRoleService).deleteProjectRole(anyLong());

        mockMvc.perform(delete("/api/v1/project-roles/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testExistsByRoleName() throws Exception {
        Mockito.when(projectRoleService.existsByRoleName(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/v1/project-roles/exists/role-name/Role A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectRolesWithAtLeastOnePermission() throws Exception {
        ProjectRole projectRole1 = new ProjectRole();
        ProjectRole projectRole2 = new ProjectRole();
        List<ProjectRole> projectRoles = Arrays.asList(projectRole1, projectRole2);

        Mockito.when(projectRoleService.findProjectRolesWithAtLeastOnePermission()).thenReturn(projectRoles);

        mockMvc.perform(get("/api/v1/project-roles/at-least-one-permission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}