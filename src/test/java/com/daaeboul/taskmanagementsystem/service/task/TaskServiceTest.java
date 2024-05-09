package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.task.TaskNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Project project;
    private Category category;
    private TaskPriority taskPriority;
    private Status status;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        project = new Project();
        ReflectionTestUtils.setField(project, "id", 1L);

        category = new Category();
        ReflectionTestUtils.setField(category, "id", 1L);

        taskPriority = new TaskPriority();
        ReflectionTestUtils.setField(taskPriority, "id", 1L);

        status = new Status();
        ReflectionTestUtils.setField(status, "id", 1L);

        task = new Task();
        ReflectionTestUtils.setField(task, "id", 1L);
        task.setTaskTitle("Test Task");
        task.setTaskDescription("Test Description");
        task.setAssignee(user);
        task.setCategory(category);
        task.setPriority(taskPriority);
        task.setProject(project);
        task.setStatus(status);
        task.setTaskDueDate(LocalDate.now().plusDays(5));
    }

    @Test
    void createTask_shouldCreateTaskSuccessfully() {
        given(taskRepository.save(task)).willReturn(task);

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isEqualTo(task);
        verify(taskRepository).save(task);
    }

    @Test
    void findTaskById_shouldReturnTaskIfFound() {
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.findTaskById(task.getId());

        assertThat(foundTask).isPresent().contains(task);
    }

    @Test
    void findTaskById_shouldReturnEmptyOptionalIfNotFound() {
        given(taskRepository.findById(100L)).willReturn(Optional.empty());

        Optional<Task> foundTask = taskService.findTaskById(100L);

        assertThat(foundTask).isEmpty();
    }

    @Test
    void findAllTasks_shouldReturnAllTasks() {
        given(taskRepository.findAll()).willReturn(List.of(task));

        List<Task> allTasks = taskService.findAllTasks();

        assertThat(allTasks).containsExactly(task);
    }

    @Test
    void updateTask_shouldUpdateTaskSuccessfully() {
        Task updatedTask = new Task();
        ReflectionTestUtils.setField(updatedTask, "id", task.getId());
        updatedTask.setTaskTitle("Updated Task");

        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));
        given(taskRepository.save(any(Task.class))).willReturn(updatedTask);

        Task result = taskService.updateTask(updatedTask);

        assertThat(result).isEqualTo(updatedTask);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowExceptionIfTaskNotFound() {
        Task updatedTask = new Task();
        ReflectionTestUtils.setField(updatedTask, "id", 100L);

        given(taskRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(updatedTask))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_shouldDeleteTaskSuccessfully() {
        given(taskRepository.existsById(task.getId())).willReturn(true);

        taskService.deleteTask(task.getId());

        verify(taskRepository).deleteById(task.getId());
    }

    @Test
    void deleteTask_shouldThrowExceptionIfTaskNotFound() {
        given(taskRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(100L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void findTasksByAssignee_shouldReturnTasksAssignedToTheGivenUser() {
        given(taskRepository.findByAssignee(user)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByAssignee(user);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByCategory_shouldReturnTasksBelongingToTheGivenCategory() {
        given(taskRepository.findByCategory(category)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByCategory(category);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByPriority_shouldReturnTasksWithTheGivenPriority() {
        given(taskRepository.findByPriority(taskPriority)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByPriority(taskPriority);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByProject_shouldReturnTasksAssociatedWithTheGivenProject() {
        given(taskRepository.findByProject(project)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByProject(project);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByStatus_shouldReturnTasksWithTheGivenStatus() {
        given(taskRepository.findByStatus(status)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByStatus(status);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksDueBefore_shouldReturnTasksDueBeforeTheGivenDate() {
        given(taskRepository.findByTaskDueDateBefore(any(LocalDate.class))).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksDueBefore(LocalDate.now());

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksDueAfter_shouldReturnTasksDueAfterTheGivenDate() {
        given(taskRepository.findByTaskDueDateAfter(any(LocalDate.class))).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksDueAfter(LocalDate.now());

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksDueBetween_shouldReturnTasksDueBetweenTheGivenDates() {
        given(taskRepository.findByTaskDueDateBetween(any(LocalDate.class), any(LocalDate.class))).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksDueBetween(LocalDate.now(), LocalDate.now());

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findIncompleteTasks_shouldReturnTasksWithNullCompletedAt() {
        given(taskRepository.findByCompletedAtIsNull()).willReturn(List.of(task));

        List<Task> tasks = taskService.findIncompleteTasks();

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findCompletedTasks_shouldReturnTasksWithNonNullCompletedAt() {
        task.setCompletedAt(LocalDateTime.now());
        given(taskRepository.findByCompletedAtIsNotNull()).willReturn(List.of(task));

        List<Task> tasks = taskService.findCompletedTasks();

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByAssigneeAndStatus_shouldReturnTasksMatchingAssigneeAndStatus() {
        given(taskRepository.findTasksByAssigneeAndStatus(user, status)).willReturn(List.of(task));

        List<Task> tasks = taskService.findTasksByAssigneeAndStatus(user, status);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findOverdueTasksByProject_shouldReturnOverdueTasksForTheProject() {
        LocalDate currentDate = LocalDate.now();  // Store the current date
        given(taskRepository.findOverdueTasksByProject(eq(project), eq(currentDate)))
                .willReturn(List.of(task));

        List<Task> tasks = taskService.findOverdueTasksByProject(project, currentDate);

        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findAllDeletedTasks_shouldReturnSoftDeletedTasks() {
        given(taskRepository.findAllDeleted()).willReturn(List.of(task));

        List<Task> tasks = taskService.findAllDeletedTasks();

        assertThat(tasks).containsExactly(task);
    }
}
