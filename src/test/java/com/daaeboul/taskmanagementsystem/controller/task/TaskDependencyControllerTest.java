package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskDependendy.TaskDependencyNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.service.task.TaskDependencyService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskDependencyController.class)
public class TaskDependencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskDependencyService taskDependencyService;

    @MockBean
    private TaskService taskService;

    private Task mockTask;
    private TaskDependency mockTaskDependency;

    @BeforeEach
    public void setup() {
        mockTask = new Task();
        ReflectionTestUtils.setField(mockTask, "id", 1L);
        mockTask.setTaskTitle("Test Task");

        TaskDependency.TaskDependencyId id = new TaskDependency.TaskDependencyId(1L, 2L);
        mockTaskDependency = new TaskDependency();
        mockTaskDependency.setId(id);
        mockTaskDependency.setTask(mockTask);
        mockTaskDependency.setDependsOnTask(mockTask);

        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(mockTask));
        Mockito.when(taskDependencyService.findTaskDependencyById(any(TaskDependency.TaskDependencyId.class)))
                .thenReturn(Optional.of(mockTaskDependency));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskDependency() throws Exception {
        Mockito.when(taskDependencyService.createTaskDependency(any(TaskDependency.class)))
                .thenReturn(mockTaskDependency);

        mockMvc.perform(post("/api/v1/task-dependencies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"taskId\": 1, \"dependsOnTaskId\": 2}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.taskId").value(1))
                .andExpect(jsonPath("$.id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskDependencyById() throws Exception {
        Mockito.when(taskDependencyService.findTaskDependencyById(any(TaskDependency.TaskDependencyId.class)))
                .thenReturn(Optional.of(mockTaskDependency));

        mockMvc.perform(get("/api/v1/task-dependencies/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.taskId").value(1))
                .andExpect(jsonPath("$.id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskDependencyById_NotFound() throws Exception {
        Mockito.when(taskDependencyService.findTaskDependencyById(any(TaskDependency.TaskDependencyId.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-dependencies/1/2"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskDependenciesByTaskId() throws Exception {
        List<TaskDependency> taskDependencies = Arrays.asList(mockTaskDependency);

        Mockito.when(taskDependencyService.findTaskDependenciesByTaskId(anyLong())).thenReturn(taskDependencies);

        mockMvc.perform(get("/api/v1/task-dependencies/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.taskId").value(1))
                .andExpect(jsonPath("$[0].id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskDependenciesByTaskIdPaged() throws Exception {
        Page<TaskDependency> taskDependenciesPage = new PageImpl<>(Arrays.asList(mockTaskDependency));

        Mockito.when(taskDependencyService.findTaskDependenciesByTaskId(anyLong(), any(Pageable.class)))
                .thenReturn(taskDependenciesPage);

        mockMvc.perform(get("/api/v1/task-dependencies/task/1/paged").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id.taskId").value(1))
                .andExpect(jsonPath("$.content[0].id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskDependenciesByDependsOnTaskId() throws Exception {
        List<TaskDependency> taskDependencies = Arrays.asList(mockTaskDependency);

        Mockito.when(taskDependencyService.findTaskDependenciesByDependsOnTaskId(anyLong())).thenReturn(taskDependencies);

        mockMvc.perform(get("/api/v1/task-dependencies/depends-on-task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.taskId").value(1))
                .andExpect(jsonPath("$[0].id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskDependency() throws Exception {
        TaskDependency updatedTaskDependency = new TaskDependency();
        TaskDependency.TaskDependencyId updatedId = new TaskDependency.TaskDependencyId(1L, 2L);
        updatedTaskDependency.setId(updatedId);
        updatedTaskDependency.setTask(mockTask);
        updatedTaskDependency.setDependsOnTask(mockTask);

        Mockito.when(taskDependencyService.updateTaskDependency(any(TaskDependency.class)))
                .thenReturn(updatedTaskDependency);

        mockMvc.perform(put("/api/v1/task-dependencies/1/2")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"taskId\": 1, \"dependsOnTaskId\": 2}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.taskId").value(1))
                .andExpect(jsonPath("$.id.dependsOnTaskId").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskDependency_NotFound() throws Exception {
        Mockito.when(taskDependencyService.updateTaskDependency(any(TaskDependency.class)))
                .thenThrow(new TaskDependencyNotFoundException("Task dependency not found"));

        mockMvc.perform(put("/api/v1/task-dependencies/1/2")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"taskId\": 1, \"dependsOnTaskId\": 2}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskDependency() throws Exception {
        Mockito.doNothing().when(taskDependencyService).deleteTaskDependency(any(TaskDependency.TaskDependencyId.class));

        mockMvc.perform(delete("/api/v1/task-dependencies/1/2")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskDependency_NotFound() throws Exception {
        Mockito.doThrow(new TaskDependencyNotFoundException("Task dependency not found"))
                .when(taskDependencyService).deleteTaskDependency(any(TaskDependency.TaskDependencyId.class));

        mockMvc.perform(delete("/api/v1/task-dependencies/1/2")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testExistsTaskDependency() throws Exception {
        Mockito.when(taskDependencyService.existsTaskDependency(anyLong(), anyLong()))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/task-dependencies/exists/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountDependenciesForTask() throws Exception {
        Mockito.when(taskDependencyService.countDependenciesForTask(anyLong()))
                .thenReturn(5L);

        mockMvc.perform(get("/api/v1/task-dependencies/count-dependencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCountDependentsForTask() throws Exception {
        Mockito.when(taskDependencyService.countDependentsForTask(anyLong()))
                .thenReturn(3L);

        mockMvc.perform(get("/api/v1/task-dependencies/count-dependents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindDirectDependenciesForTask() throws Exception {
        List<Task> dependencies = Arrays.asList(mockTask);

        Mockito.when(taskDependencyService.findDirectDependenciesForTask(anyLong()))
                .thenReturn(dependencies);

        mockMvc.perform(get("/api/v1/task-dependencies/direct-dependencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskTitle").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindDirectDependentsForTask() throws Exception {
        List<Task> dependents = Arrays.asList(mockTask);

        Mockito.when(taskDependencyService.findDirectDependentsForTask(anyLong()))
                .thenReturn(dependents);

        mockMvc.perform(get("/api/v1/task-dependencies/direct-dependents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskTitle").value("Test Task"));
    }
}
