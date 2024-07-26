package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.DuplicateTaskTypeNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.TaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.service.task.TaskTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskTypeController.class)
public class TaskTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskTypeService taskTypeService;

    private TaskType mockTaskType;

    @BeforeEach
    public void setup() {
        mockTaskType = new TaskType();
        mockTaskType.setTaskTypeName("Feature");

        Mockito.when(taskTypeService.createTaskType(any(TaskType.class))).thenReturn(mockTaskType);
        Mockito.when(taskTypeService.findTaskTypeById(anyLong())).thenReturn(Optional.of(mockTaskType));
        Mockito.when(taskTypeService.updateTaskType(any(TaskType.class))).thenReturn(mockTaskType);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskType() throws Exception {
        mockMvc.perform(post("/api/v1/task-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTypeName\": \"Feature\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTypeName").value("Feature"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskType_DuplicateName() throws Exception {
        Mockito.when(taskTypeService.createTaskType(any(TaskType.class)))
                .thenThrow(new DuplicateTaskTypeNameException("Task type name already exists: Feature"));

        mockMvc.perform(post("/api/v1/task-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTypeName\": \"Feature\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskTypeById() throws Exception {
        mockMvc.perform(get("/api/v1/task-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTypeName").value("Feature"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskTypeById_NotFound() throws Exception {
        Mockito.when(taskTypeService.findTaskTypeById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-types/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTaskTypes() throws Exception {
        TaskType taskType1 = new TaskType();
        taskType1.setTaskTypeName("Feature");

        TaskType taskType2 = new TaskType();
        taskType2.setTaskTypeName("Bug");

        List<TaskType> taskTypes = Arrays.asList(taskType1, taskType2);

        Mockito.when(taskTypeService.findAllTaskTypes()).thenReturn(taskTypes);

        mockMvc.perform(get("/api/v1/task-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTypeName").value("Feature"))
                .andExpect(jsonPath("$[1].taskTypeName").value("Bug"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskType() throws Exception {
        mockMvc.perform(put("/api/v1/task-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTypeName\": \"Updated Feature\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTypeName").value("Updated Feature"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskType_NotFound() throws Exception {
        Mockito.when(taskTypeService.findTaskTypeById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/task-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTypeName\": \"Updated Feature\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskType_DuplicateName() throws Exception {
        Mockito.when(taskTypeService.updateTaskType(any(TaskType.class)))
                .thenThrow(new DuplicateTaskTypeNameException("Task type name already exists: Feature"));

        mockMvc.perform(put("/api/v1/task-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTypeName\": \"Feature\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskType() throws Exception {
        mockMvc.perform(delete("/api/v1/task-types/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskType_NotFound() throws Exception {
        Mockito.doThrow(new TaskTypeNotFoundException("Task type not found")).when(taskTypeService).deleteTaskType(anyLong());

        mockMvc.perform(delete("/api/v1/task-types/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
