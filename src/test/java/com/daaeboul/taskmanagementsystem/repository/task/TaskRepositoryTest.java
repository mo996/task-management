package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Project project;
    private Category category;
    private TaskPriority taskPriority;
    private Status status;
    private Task task;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        project = new Project();
        project.setProjectName("Test Project");
        entityManager.persist(project);

        category = new Category();
        category.setCategoryName("Test Category");
        entityManager.persist(category);

        taskPriority = new TaskPriority();
        taskPriority.setPriorityName("High");
        entityManager.persist(taskPriority);

        status = new Status();
        status.setStatusName("In Progress");
        entityManager.persist(status);

        task = new Task();
        task.setTaskTitle("Test Task");
        task.setTaskDescription("Test Description");
        task.setAssignee(user);
        task.setCategory(category);
        task.setPriority(taskPriority);
        task.setProject(project);
        task.setStatus(status);
        task.setTaskDueDate(LocalDate.now().plusDays(5));
        entityManager.persist(task);

        entityManager.flush();
    }

    @Test
    void findByAssignee_shouldReturnTasksAssignedToTheGivenUser() {
        List<Task> tasks = taskRepository.findByAssignee(user);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByCategory_shouldReturnTasksBelongingToTheGivenCategory() {
        List<Task> tasks = taskRepository.findByCategory(category);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByPriority_shouldReturnTasksWithTheGivenPriority() {
        List<Task> tasks = taskRepository.findByPriority(taskPriority);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByProject_shouldReturnTasksAssociatedWithTheGivenProject() {
        List<Task> tasks = taskRepository.findByProject(project);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByStatus_shouldReturnTasksWithTheGivenStatus() {
        List<Task> tasks = taskRepository.findByStatus(status);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByTaskDueDateBefore_shouldReturnTasksDueBeforeTheGivenDate() {
        List<Task> tasks = taskRepository.findByTaskDueDateBefore(LocalDate.now().plusDays(10));
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByTaskDueDateAfter_shouldReturnTasksDueAfterTheGivenDate() {
        List<Task> tasks = taskRepository.findByTaskDueDateAfter(LocalDate.now());
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByTaskDueDateBetween_shouldReturnTasksDueBetweenTheGivenDates() {
        List<Task> tasks = taskRepository.findByTaskDueDateBetween(LocalDate.now(), LocalDate.now().plusDays(10));
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByCompletedAtIsNull_shouldReturnIncompleteTasks() {
        List<Task> tasks = taskRepository.findByCompletedAtIsNull();
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findByCompletedAtIsNotNull_shouldReturnCompletedTasks() {
        task.setCompletedAt(LocalDateTime.now());
        entityManager.flush();

        List<Task> tasks = taskRepository.findByCompletedAtIsNotNull();
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findTasksByAssigneeAndStatus_shouldReturnTasksMatchingAssigneeAndStatus() {
        List<Task> tasks = taskRepository.findTasksByAssigneeAndStatus(user, status);
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findOverdueTasksByProject_shouldReturnOverdueTasksForTheProject() {
        task.setTaskDueDate(LocalDate.now().minusDays(1));
        entityManager.flush();

        List<Task> tasks = taskRepository.findOverdueTasksByProject(project, LocalDate.now());
        assertThat(tasks).containsExactly(task);
    }

    @Test
    void findAllDeleted_shouldReturnSoftDeletedTasks() {
        taskRepository.deleteById(task.getId());
        entityManager.flush();

        List<Task> tasks = taskRepository.findAllDeleted();
        assertTrue(tasks.stream().anyMatch(deletedTask -> deletedTask.getId().equals(task.getId())),
                "Expected to find the deleted task by ID");
    }
}