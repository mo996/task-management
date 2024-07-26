package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.project.ProjectUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(ProjectUserController.class)
public class ProjectUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectUserService projectUserService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateProjectUser() throws Exception {
        ProjectUser projectUser = new ProjectUser();
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 1L);
        projectUser.setId(id);

        Mockito.when(projectUserService.createProjectUser(any(ProjectUser.class))).thenReturn(projectUser);

        mockMvc.perform(post("/api/v1/project-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"userId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.userId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUserById() throws Exception {
        ProjectUser projectUser = new ProjectUser();
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 1L);
        projectUser.setId(id);

        Mockito.when(projectUserService.findProjectUserById(any(ProjectUser.ProjectUserId.class))).thenReturn(Optional.of(projectUser));

        mockMvc.perform(get("/api/v1/project-users/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.userId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUserById_NotFound() throws Exception {
        Mockito.when(projectUserService.findProjectUserById(any(ProjectUser.ProjectUserId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/project-users/1/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllProjectUsers() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);
        Page<ProjectUser> page = new PageImpl<>(projectUsers);

        Mockito.when(projectUserService.findAllProjectUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUsersByProjectId() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);
        Page<ProjectUser> page = new PageImpl<>(projectUsers);

        Mockito.when(projectUserService.findProjectUsersByProjectId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-users/project/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUsersByUserId() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);
        Page<ProjectUser> page = new PageImpl<>(projectUsers);

        Mockito.when(projectUserService.findProjectUsersByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-users/user/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUserByProjectIdAndUserId() throws Exception {
        ProjectUser projectUser = new ProjectUser();
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 1L);
        projectUser.setId(id);

        Mockito.when(projectUserService.findProjectUserByProjectIdAndUserId(anyLong(), anyLong())).thenReturn(projectUser);

        mockMvc.perform(get("/api/v1/project-users/project/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.userId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUsersByProjectNameAndUserId() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);

        Mockito.when(projectUserService.findProjectUsersByProjectNameAndUserId(anyString(), anyLong())).thenReturn(projectUsers);

        mockMvc.perform(get("/api/v1/project-users/project-name/Project/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUsersByProjectIdAndProjectRoleName() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);

        Mockito.when(projectUserService.findProjectUsersByProjectIdAndProjectRoleName(anyLong(), anyString())).thenReturn(projectUsers);

        mockMvc.perform(get("/api/v1/project-users/project/1/role-name/Role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectUsersByProjectNameAndProjectRoleName() throws Exception {
        ProjectUser projectUser1 = new ProjectUser();
        ProjectUser projectUser2 = new ProjectUser();
        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);

        Mockito.when(projectUserService.findProjectUsersByProjectNameAndProjectRoleName(anyString(), anyString())).thenReturn(projectUsers);

        mockMvc.perform(get("/api/v1/project-users/project-name/Project/role-name/Role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProjectUser() throws Exception {
        ProjectUser projectUser = new ProjectUser();
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 1L);
        projectUser.setId(id);

        Mockito.when(projectUserService.updateProjectUser(any(ProjectUser.class))).thenReturn(projectUser);

        mockMvc.perform(put("/api/v1/project-users/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"userId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.userId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectUser() throws Exception {
        Mockito.doNothing().when(projectUserService).deleteProjectUser(any(ProjectUser.ProjectUserId.class));

        mockMvc.perform(delete("/api/v1/project-users/1/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUsersByProjectId() throws Exception {
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);
        Page<User> page = new PageImpl<>(users);

        Mockito.when(projectUserService.findUsersByProjectId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-users/users-by-project/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByUserId() throws Exception {
        Project project1 = new Project();
        Project project2 = new Project();
        List<Project> projects = Arrays.asList(project1, project2);
        Page<Project> page = new PageImpl<>(projects);

        Mockito.when(projectUserService.findProjectsByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-users/projects-by-user/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testExistsProjectUserByProjectIdAndUserId() throws Exception {
        Mockito.when(projectUserService.existsProjectUserByProjectIdAndUserId(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/v1/project-users/exists/project/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountProjectUsersByProjectId() throws Exception {
        Mockito.when(projectUserService.countProjectUsersByProjectId(anyLong())).thenReturn(10L);

        mockMvc.perform(get("/api/v1/project-users/count/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountProjectUsersByUserId() throws Exception {
        Mockito.when(projectUserService.countProjectUsersByUserId(anyLong())).thenReturn(10L);

        mockMvc.perform(get("/api/v1/project-users/count/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));
    }
}