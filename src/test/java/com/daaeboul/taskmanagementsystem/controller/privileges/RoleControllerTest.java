package com.daaeboul.taskmanagementsystem.controller.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.role.RoleNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import com.daaeboul.taskmanagementsystem.service.privileges.RoleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateRole() throws Exception {
        Role role = new Role();
        role.setRoleName("Admin");

        Mockito.when(roleService.createRole(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/api/v1/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\": \"Admin\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleName").value("Admin"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetRoleById() throws Exception {
        Role role = new Role();
        role.setRoleName("Admin");

        Mockito.when(roleService.findRoleById(anyLong())).thenReturn(Optional.of(role));

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Admin"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetRoleById_NotFound() throws Exception {
        Mockito.when(roleService.findRoleById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetRoleByName() throws Exception {
        Role role = new Role();
        role.setRoleName("Admin");

        Mockito.when(roleService.findRoleByName(anyString())).thenReturn(Optional.of(role));

        mockMvc.perform(get("/api/v1/roles/name/Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Admin"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetRoleByName_NotFound() throws Exception {
        Mockito.when(roleService.findRoleByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/roles/name/Admin"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllRoles() throws Exception {
        Role role1 = new Role();
        role1.setRoleName("Admin");

        Role role2 = new Role();
        role2.setRoleName("User");

        Mockito.when(roleService.findAllRoles()).thenReturn(Arrays.asList(role1, role2));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleName").value("Admin"))
                .andExpect(jsonPath("$[1].roleName").value("User"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateRole() throws Exception {
        Role role = new Role();
        role.setRoleName("Admin");

        Mockito.when(roleService.updateRole(anyLong(), any(Role.class))).thenReturn(role);

        mockMvc.perform(put("/api/v1/roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\": \"Admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("Admin"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateRole_NotFound() throws Exception {
        Mockito.when(roleService.updateRole(anyLong(), any(Role.class))).thenThrow(new RoleNotFoundException("Role not found"));

        mockMvc.perform(put("/api/v1/roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\": \"Admin\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteRole() throws Exception {
        Mockito.doNothing().when(roleService).deleteRole(anyLong());

        mockMvc.perform(delete("/api/v1/roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteRole_NotFound() throws Exception {
        Mockito.doThrow(new RoleNotFoundException("Role not found")).when(roleService).deleteRole(anyLong());

        mockMvc.perform(delete("/api/v1/roles/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
