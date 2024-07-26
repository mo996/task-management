package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User("testUser");
        ReflectionTestUtils.setField(mockUser, "id", 1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateUser() throws Exception {
        Mockito.when(userService.createUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserById() throws Exception {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserById_NotFound() throws Exception {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllUsers() throws Exception {
        List<User> users = Arrays.asList(mockUser);
        Mockito.when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllUsersWithPagination() throws Exception {
        Page<User> page = new PageImpl<>(Arrays.asList(mockUser), PageRequest.of(0, 1), 1);
        Mockito.when(userService.findAllUsers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/page")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindByUsernameContaining() throws Exception {
        List<User> users = Arrays.asList(mockUser);
        Mockito.when(userService.findByUsernameContaining(anyString())).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/search")
                        .param("username", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        User updatedUser = new User("updatedUser");
        ReflectionTestUtils.setField(updatedUser, "id", 1L);

        Mockito.when(userService.findUserById(anyLong())).thenReturn(Optional.of(mockUser));
        Mockito.when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"updatedUser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindByGroupsGroupName() throws Exception {
        List<User> users = Arrays.asList(mockUser);
        Mockito.when(userService.findByGroupsGroupName(anyString())).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/groups/testGroup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindByGroupsGroupNameWithPagination() throws Exception {
        Page<User> page = new PageImpl<>(Arrays.asList(mockUser), PageRequest.of(0, 1), 1);
        Mockito.when(userService.findByGroupsGroupName(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/groups/testGroup/page")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindByUserDetailsEmailIgnoreCase() throws Exception {
        List<User> users = Arrays.asList(mockUser);
        Mockito.when(userService.findByUserDetailsEmailIgnoreCase(anyString())).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountNonDeletedUsers() throws Exception {
        Mockito.when(userService.countNonDeletedUsers()).thenReturn(1L);

        mockMvc.perform(get("/api/v1/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllIncludingDeleted() throws Exception {
        List<User> users = Arrays.asList(mockUser);
        Mockito.when(userService.findAllIncludingDeleted()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/all-including-deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllIncludingDeletedWithPagination() throws Exception {
        Page<User> page = new PageImpl<>(Arrays.asList(mockUser), PageRequest.of(0, 1), 1);
        Mockito.when(userService.findAllIncludingDeleted(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/all-including-deleted/page")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHardDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).hardDeleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
