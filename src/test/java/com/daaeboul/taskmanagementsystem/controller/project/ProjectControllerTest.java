package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.project.ProjectNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateProject() throws Exception {
        Project project = new Project();
        project.setProjectName("Project A");

        Mockito.when(projectService.createProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectName\": \"Project A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName").value("Project A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetProjectById() throws Exception {
        Project project = new Project();
        project.setProjectName("Project A");

        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Project A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetProjectById_NotFound() throws Exception {
        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllProjects() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findAllProjects()).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllDeletedProjects() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findAllDeletedProjects()).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects/deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByStartDateBefore() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findProjectsByStartDateBefore(any(LocalDateTime.class))).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects/startDateBefore/2022-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByEndDateAfter() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findProjectsByEndDateAfter(any(LocalDateTime.class))).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects/endDateAfter/2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByStartDateBetween() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findProjectsByStartDateBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects/startDateBetween")
                        .param("startDate", "2022-01-01")
                        .param("endDate", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByNameContaining() throws Exception {
        Project project1 = new Project();
        project1.setProjectName("Project A");

        Project project2 = new Project();
        project2.setProjectName("Project B");

        Mockito.when(projectService.findProjectsByNameContaining(anyString())).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/v1/projects/nameContaining/Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProject() throws Exception {
        Project project = new Project();
        project.setProjectName("Project A");

        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(Optional.of(project));
        Mockito.when(projectService.updateProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(put("/api/v1/projects/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectName\": \"Project A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Project A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProject_NotFound() throws Exception {
        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/projects/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectName\": \"Project A\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProject() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(anyLong());

        mockMvc.perform(delete("/api/v1/projects/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProject_NotFound() throws Exception {
        Mockito.doThrow(new ProjectNotFoundException("Project not found")).when(projectService).deleteProject(anyLong());

        mockMvc.perform(delete("/api/v1/projects/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHardDeleteProject() throws Exception {
        Mockito.doNothing().when(projectService).hardDeleteProject(anyLong());

        mockMvc.perform(delete("/api/v1/projects/1/hard")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHardDeleteProject_NotFound() throws Exception {
        Mockito.doThrow(new ProjectNotFoundException("Project not found")).when(projectService).hardDeleteProject(anyLong());

        mockMvc.perform(delete("/api/v1/projects/1/hard")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}