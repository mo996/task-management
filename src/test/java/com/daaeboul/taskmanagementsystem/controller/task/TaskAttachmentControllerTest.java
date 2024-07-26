package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import com.daaeboul.taskmanagementsystem.service.task.TaskAttachmentService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskAttachmentController.class)
public class TaskAttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskAttachmentService taskAttachmentService;

    @MockBean
    private TaskService taskService;

    private TaskAttachment taskAttachment;
    private Task task;

    @BeforeEach
    public void setup() {
        task = new Task();
        ReflectionTestUtils.setField(task, "id", 1L);
        task.setTaskTitle("Test Task");

        taskAttachment = new TaskAttachment();
        ReflectionTestUtils.setField(taskAttachment, "id", 1L);
        taskAttachment.setFileName("Test File");
        taskAttachment.setFileType("text/plain");
        taskAttachment.setFileSize(1024L);
        taskAttachment.setFileContent("Test Content".getBytes());
        taskAttachment.setTask(task);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskAttachment() throws Exception {
        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(task));
        Mockito.when(taskAttachmentService.createTaskAttachment(any(Task.class), any(TaskAttachment.class)))
                .thenReturn(taskAttachment);

        String base64Content = Base64.getEncoder().encodeToString("Test Content".getBytes());

        mockMvc.perform(post("/api/v1/task-attachments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\": \"Test File\", \"fileType\": \"text/plain\", \"fileSize\": 1024, \"fileContent\": \"" + base64Content + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("Test File"))
                .andExpect(jsonPath("$.fileType").value("text/plain"))
                .andExpect(jsonPath("$.fileSize").value(1024));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskAttachmentById() throws Exception {
        Mockito.when(taskAttachmentService.findTaskAttachmentById(anyLong())).thenReturn(Optional.of(taskAttachment));

        mockMvc.perform(get("/api/v1/task-attachments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("Test File"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskAttachmentsByTaskId() throws Exception {
        Mockito.when(taskAttachmentService.findTaskAttachmentsByTaskId(anyLong())).thenReturn(Collections.singletonList(taskAttachment));

        mockMvc.perform(get("/api/v1/task-attachments/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fileName").value("Test File"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskAttachmentsByFileName() throws Exception {
        Mockito.when(taskAttachmentService.findTaskAttachmentsByFileName(any())).thenReturn(Collections.singletonList(taskAttachment));

        mockMvc.perform(get("/api/v1/task-attachments/search/file-name/Test File"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fileName").value("Test File"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskAttachment() throws Exception {
        Mockito.when(taskAttachmentService.findTaskAttachmentById(anyLong())).thenReturn(Optional.of(taskAttachment));
        Mockito.when(taskAttachmentService.updateTaskAttachment(any(TaskAttachment.class))).thenReturn(taskAttachment);

        String base64Content = Base64.getEncoder().encodeToString("Updated Content".getBytes());

        mockMvc.perform(put("/api/v1/task-attachments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\": \"Updated File\", \"fileType\": \"text/plain\", \"fileSize\": 2048, \"fileContent\": \"" + base64Content + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("Updated File"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskAttachment() throws Exception {
        Mockito.doNothing().when(taskAttachmentService).deleteTaskAttachment(anyLong());

        mockMvc.perform(delete("/api/v1/task-attachments/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
