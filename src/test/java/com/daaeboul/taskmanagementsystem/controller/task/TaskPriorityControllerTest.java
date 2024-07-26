package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.DuplicateTaskPriorityNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.TaskPriorityNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.service.task.TaskPriorityService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskPriorityController.class)
public class TaskPriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskPriorityService taskPriorityService;

    private TaskPriority mockTaskPriority;

    @BeforeEach
    public void setup() {
        mockTaskPriority = new TaskPriority();
        mockTaskPriority.setPriorityName("High");

        Mockito.when(taskPriorityService.createTaskPriority(any(TaskPriority.class))).thenReturn(mockTaskPriority);
        Mockito.when(taskPriorityService.findTaskPriorityById(anyLong())).thenReturn(Optional.of(mockTaskPriority));
        Mockito.when(taskPriorityService.updateTaskPriority(any(TaskPriority.class))).thenReturn(mockTaskPriority);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskPriority() throws Exception {
        mockMvc.perform(post("/api/v1/task-priorities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priorityName\": \"High\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityName").value("High"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskPriority_DuplicateName() throws Exception {
        Mockito.when(taskPriorityService.createTaskPriority(any(TaskPriority.class)))
                .thenThrow(new DuplicateTaskPriorityNameException("Task priority name already exists: High"));

        mockMvc.perform(post("/api/v1/task-priorities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priorityName\": \"High\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskPriorityById() throws Exception {
        mockMvc.perform(get("/api/v1/task-priorities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityName").value("High"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskPriorityById_NotFound() throws Exception {
        Mockito.when(taskPriorityService.findTaskPriorityById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-priorities/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTaskPriorities() throws Exception {
        TaskPriority taskPriority1 = new TaskPriority();
        taskPriority1.setPriorityName("High");

        TaskPriority taskPriority2 = new TaskPriority();
        taskPriority2.setPriorityName("Medium");

        List<TaskPriority> taskPriorities = Arrays.asList(taskPriority1, taskPriority2);

        Mockito.when(taskPriorityService.findAllTaskPriorities()).thenReturn(taskPriorities);

        mockMvc.perform(get("/api/v1/task-priorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priorityName").value("High"))
                .andExpect(jsonPath("$[1].priorityName").value("Medium"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskPriority() throws Exception {
        mockMvc.perform(put("/api/v1/task-priorities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priorityName\": \"Updated High\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityName").value("Updated High"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskPriority_NotFound() throws Exception {
        Mockito.when(taskPriorityService.findTaskPriorityById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/task-priorities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priorityName\": \"Updated High\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskPriority_DuplicateName() throws Exception {
        Mockito.when(taskPriorityService.updateTaskPriority(any(TaskPriority.class)))
                .thenThrow(new DuplicateTaskPriorityNameException("Task priority name already exists: High"));

        mockMvc.perform(put("/api/v1/task-priorities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priorityName\": \"High\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskPriority() throws Exception {
        mockMvc.perform(delete("/api/v1/task-priorities/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskPriority_NotFound() throws Exception {
        Mockito.doThrow(new TaskPriorityNotFoundException("Task priority not found")).when(taskPriorityService).deleteTaskPriority(anyLong());

        mockMvc.perform(delete("/api/v1/task-priorities/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
