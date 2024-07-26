package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskComment.TaskCommentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.task.TaskCommentService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
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

@WebMvcTest(TaskCommentController.class)
public class TaskCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskCommentService taskCommentService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private Task mockTask;
    private User mockUser;
    private TaskComment mockTaskComment;

    @BeforeEach
    public void setup() {
        mockTask = new Task();
        ReflectionTestUtils.setField(mockTask, "id", 1L);
        mockTask.setTaskTitle("Test Task");

        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        mockUser.setUsername("testuser");

        mockTaskComment = new TaskComment();
        ReflectionTestUtils.setField(mockTaskComment, "id", 1L);
        mockTaskComment.setComment("Test Comment");

        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(mockTask));
        Mockito.when(userService.findUserById(anyLong())).thenReturn(Optional.of(mockUser));
        Mockito.when(taskCommentService.findTaskCommentById(anyLong())).thenReturn(Optional.of(mockTaskComment));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTaskComment() throws Exception {
        Mockito.when(taskCommentService.createTaskComment(any(Task.class), any(User.class), any(TaskComment.class)))
                .thenReturn(mockTaskComment);

        mockMvc.perform(post("/api/v1/task-comments")
                        .param("taskId", "1")
                        .param("userId", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"New Comment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentById() throws Exception {
        Mockito.when(taskCommentService.findTaskCommentById(anyLong())).thenReturn(Optional.of(mockTaskComment));

        mockMvc.perform(get("/api/v1/task-comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentById_NotFound() throws Exception {
        Mockito.when(taskCommentService.findTaskCommentById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-comments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTaskComments() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findAllTaskComments()).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByTask() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findTaskCommentsByTask(any(Task.class))).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByTaskPaged() throws Exception {
        Page<TaskComment> commentsPage = new PageImpl<>(Arrays.asList(mockTaskComment));

        Mockito.when(taskCommentService.findTaskCommentsByTask(any(Task.class), any(Pageable.class))).thenReturn(commentsPage);

        mockMvc.perform(get("/api/v1/task-comments/task/1/paged").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByUser() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findTaskCommentsByUser(any(User.class))).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByUserPaged() throws Exception {
        Page<TaskComment> commentsPage = new PageImpl<>(Arrays.asList(mockTaskComment));

        Mockito.when(taskCommentService.findTaskCommentsByUser(any(User.class), any(Pageable.class))).thenReturn(commentsPage);

        mockMvc.perform(get("/api/v1/task-comments/user/1/paged").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByKeyword() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findTaskCommentsByKeyword(anyString())).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments/search").param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskCommentsByKeywordPaged() throws Exception {
        Page<TaskComment> commentsPage = new PageImpl<>(Arrays.asList(mockTaskComment));

        Mockito.when(taskCommentService.findTaskCommentsByKeyword(anyString(), any(Pageable.class))).thenReturn(commentsPage);

        mockMvc.perform(get("/api/v1/task-comments/search/paged").param("keyword", "Test").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskComment() throws Exception {
        TaskComment updatedComment = new TaskComment();
        updatedComment.setComment("Updated Comment");

        Mockito.when(taskCommentService.findTaskCommentById(anyLong())).thenReturn(Optional.of(mockTaskComment));
        Mockito.when(taskCommentService.updateTaskComment(any(TaskComment.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/v1/task-comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Updated Comment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTaskComment_NotFound() throws Exception {
        Mockito.when(taskCommentService.findTaskCommentById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/task-comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Updated Comment\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskComment() throws Exception {
        Mockito.doNothing().when(taskCommentService).deleteTaskComment(anyLong());

        mockMvc.perform(delete("/api/v1/task-comments/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTaskComment_NotFound() throws Exception {
        Mockito.doThrow(new TaskCommentNotFoundException("Task comment not found")).when(taskCommentService).deleteTaskComment(anyLong());

        mockMvc.perform(delete("/api/v1/task-comments/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTaskCommentsIncludingDeleted() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findAllTaskCommentsIncludingDeleted()).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments/deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTaskCommentsByTaskIdIncludingDeleted() throws Exception {
        List<TaskComment> comments = Arrays.asList(mockTaskComment);

        Mockito.when(taskCommentService.findAllTaskCommentsByTaskIdIncludingDeleted(anyLong())).thenReturn(comments);

        mockMvc.perform(get("/api/v1/task-comments/task/1/deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test Comment"));
    }
}
