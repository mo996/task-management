package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.task.TaskNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.project.ProjectService;
import com.daaeboul.taskmanagementsystem.service.task.CategoryService;
import com.daaeboul.taskmanagementsystem.service.task.TaskPriorityService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import com.daaeboul.taskmanagementsystem.service.transition.StatusService;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private StatusService statusService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private TaskPriorityService taskPriorityService;

    private User mockUser;
    private Project mockProject;
    private Status mockStatus;
    private Category mockCategory;
    private TaskPriority mockTaskPriority;
    private Task mockTask;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        mockUser.setUsername("testuser");

        mockProject = new Project();
        ReflectionTestUtils.setField(mockProject, "id", 1L);
        mockProject.setProjectName("Test Project");

        mockStatus = new Status();
        ReflectionTestUtils.setField(mockStatus, "id", 1L);
        mockStatus.setStatusName("Test Status");

        mockCategory = new Category();
        ReflectionTestUtils.setField(mockCategory, "id", 1L);
        mockCategory.setCategoryName("Test Category");

        mockTaskPriority = new TaskPriority();
        ReflectionTestUtils.setField(mockTaskPriority, "id", 1L);
        mockTaskPriority.setPriorityName("High");

        mockTask = new Task();
        ReflectionTestUtils.setField(mockTask, "id", 1L);
        mockTask.setTaskTitle("Test Task");

        Mockito.when(userService.findUserById(anyLong())).thenReturn(Optional.of(mockUser));
        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(Optional.of(mockProject));
        Mockito.when(statusService.findStatusById(anyLong())).thenReturn(Optional.of(mockStatus));
        Mockito.when(categoryService.findCategoryById(anyLong())).thenReturn(Optional.of(mockCategory));
        Mockito.when(taskPriorityService.findTaskPriorityById(anyLong())).thenReturn(Optional.of(mockTaskPriority));
        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(mockTask));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setTaskTitle("Task A");

        Mockito.when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTitle\": \"Task A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTitle").value("Task A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskById() throws Exception {
        Task task = new Task();
        task.setTaskTitle("Task A");

        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTitle").value("Task A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTaskById_NotFound() throws Exception {
        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllTasks() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTask() throws Exception {
        Task existingTask = new Task();
        existingTask.setTaskTitle("Existing Task");

        Task updatedTask = new Task();
        updatedTask.setTaskTitle("Updated Task");

        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.of(existingTask));
        Mockito.when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTitle\": \"Updated Task\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTitle").value("Updated Task"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateTask_NotFound() throws Exception {
        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskTitle\": \"Updated Task\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTask() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(anyLong());

        mockMvc.perform(delete("/api/v1/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteTask_NotFound() throws Exception {
        Mockito.doThrow(new TaskNotFoundException("Task not found")).when(taskService).deleteTask(anyLong());

        mockMvc.perform(delete("/api/v1/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksByAssignee() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksByAssignee(any(User.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/assignee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksByCategory() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksByCategory(any(Category.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksByPriority() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksByPriority(any(TaskPriority.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/priority/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksByStatus() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksByStatus(any(Status.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksDueBefore() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksDueBefore(any(LocalDate.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/due-before/2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksDueAfter() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksDueAfter(any(LocalDate.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/due-after/2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksDueBetween() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksDueBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/due-between")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindIncompleteTasks() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findIncompleteTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCompletedTasks() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findCompletedTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindTasksByAssigneeAndStatus() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findTasksByAssigneeAndStatus(any(User.class), any(Status.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/assignee/1/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindOverdueTasksByProject() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findOverdueTasksByProject(any(Project.class), any(LocalDate.class))).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/overdue/project/1/date/2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllDeletedTasks() throws Exception {
        Task task1 = new Task();
        task1.setTaskTitle("Task A");

        Task task2 = new Task();
        task2.setTaskTitle("Task B");

        List<Task> tasks = Arrays.asList(task1, task2);

        Mockito.when(taskService.findAllDeletedTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskTitle").value("Task A"))
                .andExpect(jsonPath("$[1].taskTitle").value("Task B"));
    }
}
