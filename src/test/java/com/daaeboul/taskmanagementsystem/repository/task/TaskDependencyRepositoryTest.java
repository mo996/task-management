package com.daaeboul.taskmanagementsystem.repository.task;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskDependencyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    private Task task1, task2, task3;
    private TaskDependency dependency12, dependency23;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testUser");
        user = entityManager.persistFlushFind(user);

        Project project = new Project();
        project.setProjectName("Test Project Name");
        project = entityManager.persistFlushFind(project);

        TaskType taskType = new TaskType();
        taskType.setTaskTypeName("testBug");
        taskType = entityManager.persistFlushFind(taskType);

        Workflow workflow =  new Workflow();
        workflow.setName("New Workflow");
        workflow = entityManager.persistFlushFind(workflow);

        TaskPriority priority = new TaskPriority();
        priority.setPriorityName("testHigh");
        priority = entityManager.persistFlushFind(priority);

        Status taskStatus = new Status();
        taskStatus.setStatusName("testInProgress");
        taskStatus = entityManager.persistFlushFind(taskStatus);

        ProjectTaskType projectTaskType = new ProjectTaskType();
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(project.getId(), taskType.getId());
        projectTaskType.setId(id);
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        projectTaskType.setWorkflow(workflow);
        projectTaskType = entityManager.persistFlushFind(projectTaskType);

        task1 = new Task();
        task1.setAssignee(user);
        task1.setProject(project);
        task1.setTaskType(taskType);
        task1.setPriority(priority);
        task1.setStatus(taskStatus);
        task1.setTaskTitle("Sample Task");
        task1.setTaskDescription("This is a sample task.");
        task1.setTaskDueDate(LocalDate.now().plusDays(7));
        task1 = entityManager.persistFlushFind(task1);

        task2 = new Task();
        task2.setAssignee(user);
        task2.setProject(project);
        task2.setTaskType(taskType);
        task2.setPriority(priority);
        task2.setStatus(taskStatus);
        task2.setTaskTitle("Sample Task2");
        task2.setTaskDescription("This is a sample task2.");
        task2.setTaskDueDate(LocalDate.now().plusDays(7));
        task2 = entityManager.persistFlushFind(task2);

        task3 = new Task();
        task3.setAssignee(user);
        task3.setProject(project);
        task3.setTaskType(taskType);
        task3.setPriority(priority);
        task3.setStatus(taskStatus);
        task3.setTaskTitle("Sample Task2");
        task3.setTaskDescription("This is a sample task3.");
        task3.setTaskDueDate(LocalDate.now().plusDays(7));
        task3 = entityManager.persistFlushFind(task3);

        dependency12 = new TaskDependency();
        dependency12.setId(new TaskDependency.TaskDependencyId(task1.getId(), task2.getId()));
        dependency12.setTask(task1);
        dependency12.setDependsOnTask(task2);
        entityManager.persistFlushFind(dependency12);

        dependency23 = new TaskDependency();
        dependency23.setId(new TaskDependency.TaskDependencyId(task2.getId(), task3.getId()));
        dependency23.setTask(task2);
        dependency23.setDependsOnTask(task3);
        entityManager.persistFlushFind(dependency23);
    }

    @Test
    void findByIdTaskId_shouldReturnDependenciesForTask() {
        List<TaskDependency> dependencies = taskDependencyRepository.findByIdTaskId(task1.getId());
        System.out.println(dependencies.size());
        System.out.println(dependencies.get(0).getTask().getTaskTitle());
        System.out.println(dependencies.get(0).getDependsOnTask().getTaskTitle());

        assertThat(dependencies)
                .extracting(td -> new TaskDependency.TaskDependencyId(td.getTask().getId(), td.getDependsOnTask().getId()))
                .containsExactly(dependency12.getId());
    }

    @Test
    void findByIdTaskIdWithPageable_shouldReturnPaginatedDependenciesForTask() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskDependency> dependencies = taskDependencyRepository.findByIdTaskId(task1.getId(), pageable);
        assertThat(dependencies.getContent()).hasSize(1);
    }

    @Test
    void findByIdDependsOnTaskId_shouldReturnDependenciesOnTask() {
        List<TaskDependency> dependencies = taskDependencyRepository.findByIdDependsOnTaskId(task2.getId());
        assertThat(dependencies)
                .extracting(td -> new TaskDependency.TaskDependencyId(td.getTask().getId(), td.getDependsOnTask().getId()))
                .containsExactly(new TaskDependency.TaskDependencyId(task1.getId(), task2.getId()));
    }

    @Test
    void findByIdTaskIdAndIdDependsOnTaskId_shouldReturnDependencyIfFound() {
        Optional<TaskDependency> dependency = taskDependencyRepository.findByIdTaskIdAndIdDependsOnTaskId(task1.getId(), task2.getId());
        assertThat(dependency).isPresent();

        // Compare the IDs to confirm the correct dependency is found
        assertThat(dependency.get().getId()).isEqualTo(new TaskDependency.TaskDependencyId(task1.getId(), task2.getId()));
    }

    @Test
    void findByIdTaskIdAndIdDependsOnTaskId_shouldReturnEmptyOptionalIfNotFound() {
        Optional<TaskDependency> dependency = taskDependencyRepository.findByIdTaskIdAndIdDependsOnTaskId(-1L, -1L);
        assertThat(dependency).isEmpty();
    }

    @Test
    void existsByIdTaskIdAndIdDependsOnTaskId_shouldReturnTrueIfDependencyExists() {
        boolean exists = taskDependencyRepository.existsByIdTaskIdAndIdDependsOnTaskId(task1.getId(), task2.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdTaskIdAndIdDependsOnTaskId_shouldReturnFalseIfDependencyDoesNotExist() {
        boolean exists = taskDependencyRepository.existsByIdTaskIdAndIdDependsOnTaskId(-1L, -1L);
        assertThat(exists).isFalse();
    }

    @Test
    void countByIdTaskId_shouldReturnCountOfDependenciesForTask() {
        long count = taskDependencyRepository.countByIdTaskId(task1.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByIdDependsOnTaskId_shouldReturnCountOfDependentsForTask() {
        long count = taskDependencyRepository.countByIdDependsOnTaskId(task2.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void findDirectDependenciesForTask_shouldReturnDirectDependencies() {
        List<Task> dependencies = taskDependencyRepository.findDirectDependenciesForTask(task1.getId());
        assertThat(dependencies).containsExactly(task2);
    }

    @Test
    void findDirectDependentsForTask_shouldReturnDirectDependents() {
        List<Task> dependents = taskDependencyRepository.findDirectDependentsForTask(task2.getId());
        assertThat(dependents).containsExactly(task1);
    }
}