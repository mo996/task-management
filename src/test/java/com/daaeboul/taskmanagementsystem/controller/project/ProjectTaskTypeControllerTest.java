package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectTaskType.ProjectTaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.service.project.ProjectTaskTypeService;
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

@WebMvcTest(ProjectTaskTypeController.class)
public class ProjectTaskTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectTaskTypeService projectTaskTypeService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateProjectTaskType() throws Exception {
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setId(new ProjectTaskType.ProjectTaskTypeId(1L, 1L));

        Mockito.when(projectTaskTypeService.createProjectTaskType(any(ProjectTaskType.class))).thenReturn(projectTaskType);

        mockMvc.perform(post("/api/v1/project-task-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"taskTypeId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.taskTypeId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectTaskTypeById() throws Exception {
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setId(new ProjectTaskType.ProjectTaskTypeId(1L, 1L));

        Mockito.when(projectTaskTypeService.findProjectTaskTypeById(any(ProjectTaskType.ProjectTaskTypeId.class))).thenReturn(Optional.of(projectTaskType));

        mockMvc.perform(get("/api/v1/project-task-types/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.taskTypeId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectTaskTypeById_NotFound() throws Exception {
        Mockito.when(projectTaskTypeService.findProjectTaskTypeById(any(ProjectTaskType.ProjectTaskTypeId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/project-task-types/1/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllProjectTaskTypes() throws Exception {
        ProjectTaskType projectTaskType1 = new ProjectTaskType();
        ProjectTaskType projectTaskType2 = new ProjectTaskType();
        List<ProjectTaskType> projectTaskTypes = Arrays.asList(projectTaskType1, projectTaskType2);

        Mockito.when(projectTaskTypeService.findAllProjectTaskTypes()).thenReturn(projectTaskTypes);

        mockMvc.perform(get("/api/v1/project-task-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectTaskTypesByProjectName() throws Exception {
        ProjectTaskType projectTaskType1 = new ProjectTaskType();
        ProjectTaskType projectTaskType2 = new ProjectTaskType();
        List<ProjectTaskType> projectTaskTypes = Arrays.asList(projectTaskType1, projectTaskType2);

        Mockito.when(projectTaskTypeService.findProjectTaskTypesByProjectName(anyString())).thenReturn(projectTaskTypes);

        mockMvc.perform(get("/api/v1/project-task-types/project-name/ProjectA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectTaskTypesByTaskTypeName() throws Exception {
        ProjectTaskType projectTaskType1 = new ProjectTaskType();
        ProjectTaskType projectTaskType2 = new ProjectTaskType();
        List<ProjectTaskType> projectTaskTypes = Arrays.asList(projectTaskType1, projectTaskType2);

        Mockito.when(projectTaskTypeService.findProjectTaskTypesByTaskTypeName(anyString())).thenReturn(projectTaskTypes);

        mockMvc.perform(get("/api/v1/project-task-types/task-type-name/TaskTypeA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectTaskTypesByWorkflowName() throws Exception {
        ProjectTaskType projectTaskType1 = new ProjectTaskType();
        ProjectTaskType projectTaskType2 = new ProjectTaskType();
        List<ProjectTaskType> projectTaskTypes = Arrays.asList(projectTaskType1, projectTaskType2);

        Mockito.when(projectTaskTypeService.findProjectTaskTypesByWorkflowName(anyString())).thenReturn(projectTaskTypes);

        mockMvc.perform(get("/api/v1/project-task-types/workflow-name/WorkflowA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsUsingWorkflowByName() throws Exception {
        Project project1 = new Project();
        Project project2 = new Project();
        List<Project> projects = Arrays.asList(project1, project2);

        Mockito.when(projectTaskTypeService.findProjectsUsingWorkflowByName(anyString())).thenReturn(projects);

        mockMvc.perform(get("/api/v1/project-task-types/projects-using-workflow/WorkflowA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskTypesByProjectName() throws Exception {
        TaskType taskType1 = new TaskType();
        TaskType taskType2 = new TaskType();
        List<TaskType> taskTypes = Arrays.asList(taskType1, taskType2);

        Mockito.when(projectTaskTypeService.findTaskTypesByProjectName(anyString())).thenReturn(taskTypes);

        mockMvc.perform(get("/api/v1/project-task-types/task-types-by-project/ProjectA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountDistinctTaskTypesByProjectName() throws Exception {
        Mockito.when(projectTaskTypeService.countDistinctTaskTypesByProjectName(anyString())).thenReturn(5L);

        mockMvc.perform(get("/api/v1/project-task-types/count-task-types-by-project/ProjectA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProjectTaskType() throws Exception {
        ProjectTaskType existingProjectTaskType = new ProjectTaskType();
        existingProjectTaskType.setId(new ProjectTaskType.ProjectTaskTypeId(1L, 1L));

        ProjectTaskType updatedProjectTaskType = new ProjectTaskType();
        updatedProjectTaskType.setId(new ProjectTaskType.ProjectTaskTypeId(1L, 1L));

        Mockito.when(projectTaskTypeService.findProjectTaskTypeById(any(ProjectTaskType.ProjectTaskTypeId.class))).thenReturn(Optional.of(existingProjectTaskType));
        Mockito.when(projectTaskTypeService.updateProjectTaskType(any(ProjectTaskType.class))).thenReturn(updatedProjectTaskType);

        mockMvc.perform(put("/api/v1/project-task-types/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"taskTypeId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.taskTypeId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectTaskType() throws Exception {
        Mockito.doNothing().when(projectTaskTypeService).deleteProjectTaskType(any(ProjectTaskType.ProjectTaskTypeId.class));

        mockMvc.perform(delete("/api/v1/project-task-types/1/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectTaskType_NotFound() throws Exception {
        Mockito.doThrow(new ProjectTaskTypeNotFoundException("Project-TaskType association not found")).when(projectTaskTypeService).deleteProjectTaskType(any(ProjectTaskType.ProjectTaskTypeId.class));

        mockMvc.perform(delete("/api/v1/project-task-types/1/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}