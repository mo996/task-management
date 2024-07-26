package com.daaeboul.taskmanagementsystem.controller.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.permission.PermissionNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.service.privileges.PermissionService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreatePermission() throws Exception {
        Permission permission = new Permission();
        permission.setPermissionName("READ");

        Mockito.when(permissionService.createPermission(any(Permission.class))).thenReturn(permission);

        mockMvc.perform(post("/api/v1/permissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionName\": \"READ\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.permissionName").value("READ"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetPermissionById() throws Exception {
        Permission permission = new Permission();
        permission.setPermissionName("READ");

        Mockito.when(permissionService.findPermissionById(anyLong())).thenReturn(Optional.of(permission));

        mockMvc.perform(get("/api/v1/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissionName").value("READ"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetPermissionById_NotFound() throws Exception {
        Mockito.when(permissionService.findPermissionById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/permissions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetPermissionByName() throws Exception {
        Permission permission = new Permission();
        permission.setPermissionName("READ");

        Mockito.when(permissionService.findPermissionByName(anyString())).thenReturn(Optional.of(permission));

        mockMvc.perform(get("/api/v1/permissions/name/READ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissionName").value("READ"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetPermissionByName_NotFound() throws Exception {
        Mockito.when(permissionService.findPermissionByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/permissions/name/READ"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllPermissions() throws Exception {
        Permission permission1 = new Permission();
        permission1.setPermissionName("READ");

        Permission permission2 = new Permission();
        permission2.setPermissionName("WRITE");

        Mockito.when(permissionService.findAllPermissions()).thenReturn(Arrays.asList(permission1, permission2));

        mockMvc.perform(get("/api/v1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].permissionName").value("READ"))
                .andExpect(jsonPath("$[1].permissionName").value("WRITE"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdatePermission() throws Exception {
        Permission permission = new Permission();
        permission.setPermissionName("READ");

        Mockito.when(permissionService.findPermissionById(anyLong())).thenReturn(Optional.of(permission));
        Mockito.when(permissionService.updatePermission(any(Permission.class))).thenReturn(permission);

        mockMvc.perform(put("/api/v1/permissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionName\": \"READ\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissionName").value("READ"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdatePermission_NotFound() throws Exception {
        Mockito.when(permissionService.findPermissionById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/permissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionName\": \"READ\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeletePermission() throws Exception {
        Mockito.doNothing().when(permissionService).deletePermission(anyLong());

        mockMvc.perform(delete("/api/v1/permissions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeletePermission_NotFound() throws Exception {
        Mockito.doThrow(new PermissionNotFoundException("Permission not found")).when(permissionService).deletePermission(anyLong());

        mockMvc.perform(delete("/api/v1/permissions/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }


}
