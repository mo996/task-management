package com.daaeboul.taskmanagementsystem.service.task;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskDependendy.TaskDependencyNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.repository.task.TaskDependencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDependencyServiceTest {

    @Mock
    private TaskDependencyRepository taskDependencyRepository;

    @InjectMocks
    private TaskDependencyService taskDependencyService;

    private Task task1;
    private Task task2;
    private TaskDependency dependency12;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task2 = new Task();
        dependency12 = new TaskDependency();
        dependency12.setTask(task1);
        dependency12.setDependsOnTask(task2);

        ReflectionTestUtils.setField(task1, "id", 1L);
        ReflectionTestUtils.setField(task2, "id", 2L);
        ReflectionTestUtils.setField(dependency12, "id", new TaskDependency.TaskDependencyId(1L, 2L));
    }

    @Test
    void createTaskDependency_shouldCreateDependencySuccessfully() {
        given(taskDependencyRepository.save(dependency12)).willReturn(dependency12);

        TaskDependency createdDependency = taskDependencyService.createTaskDependency(dependency12);

        assertThat(createdDependency).isEqualTo(dependency12);
        verify(taskDependencyRepository).save(dependency12);
    }

    @Test
    void findTaskDependencyById_shouldReturnDependencyIfFound() {
        given(taskDependencyRepository.findById(dependency12.getId())).willReturn(Optional.of(dependency12));

        Optional<TaskDependency> foundDependency = taskDependencyService.findTaskDependencyById(dependency12.getId());

        assertThat(foundDependency).isPresent().contains(dependency12);
    }

    @Test
    void findTaskDependencyById_shouldReturnEmptyOptionalIfNotFound() {
        TaskDependency.TaskDependencyId nonExistentId = new TaskDependency.TaskDependencyId(999L, 999L);
        given(taskDependencyRepository.findById(nonExistentId)).willReturn(Optional.empty());

        Optional<TaskDependency> foundDependency = taskDependencyService.findTaskDependencyById(nonExistentId);

        assertThat(foundDependency).isEmpty();
    }

    @Test
    void findTaskDependenciesByTaskId_shouldReturnDependenciesForTask() {
        given(taskDependencyRepository.findByIdTaskId(task1.getId())).willReturn(List.of(dependency12));

        List<TaskDependency> dependencies = taskDependencyService.findTaskDependenciesByTaskId(task1.getId());

        assertThat(dependencies).containsExactly(dependency12);
    }

    @Test
    void findTaskDependenciesByTaskIdWithPageable_shouldReturnPaginatedDependenciesForTask() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskDependency> dependencyPage = new PageImpl<>(List.of(dependency12), pageable, 1);
        given(taskDependencyRepository.findByIdTaskId(task1.getId(), pageable)).willReturn(dependencyPage);

        Page<TaskDependency> result = taskDependencyService.findTaskDependenciesByTaskId(task1.getId(), pageable);

        assertThat(result).isEqualTo(dependencyPage);
    }

    @Test
    void findTaskDependenciesByDependsOnTaskId_shouldReturnDependenciesOnTask() {
        given(taskDependencyRepository.findByIdDependsOnTaskId(task2.getId())).willReturn(List.of(dependency12));

        List<TaskDependency> dependencies = taskDependencyService.findTaskDependenciesByDependsOnTaskId(task2.getId());

        assertThat(dependencies).containsExactly(dependency12);
    }

    @Test
    void findTaskDependencyByTaskIds_shouldReturnDependencyIfFound() {
        given(taskDependencyRepository.findByIdTaskIdAndIdDependsOnTaskId(task1.getId(), task2.getId())).willReturn(Optional.of(dependency12));

        Optional<TaskDependency> dependency = taskDependencyService.findTaskDependencyByTaskIds(task1.getId(), task2.getId());

        assertThat(dependency).isPresent().contains(dependency12);
    }

    @Test
    void findTaskDependencyByTaskIds_shouldReturnEmptyOptionalIfNotFound() {
        given(taskDependencyRepository.findByIdTaskIdAndIdDependsOnTaskId(-1L, -1L)).willReturn(Optional.empty());

        Optional<TaskDependency> dependency = taskDependencyService.findTaskDependencyByTaskIds(-1L, -1L);

        assertThat(dependency).isEmpty();
    }

    @Test
    void updateTaskDependency_shouldUpdateDependencySuccessfully() {
        TaskDependency updatedDependency = new TaskDependency();
        updatedDependency.setId(dependency12.getId());
        // ... update other fields as needed

        given(taskDependencyRepository.findById(dependency12.getId())).willReturn(Optional.of(dependency12));
        given(taskDependencyRepository.save(any(TaskDependency.class))).willReturn(updatedDependency);

        TaskDependency result = taskDependencyService.updateTaskDependency(updatedDependency);

        assertThat(result).isEqualTo(updatedDependency);
        verify(taskDependencyRepository).save(any(TaskDependency.class));
    }

    @Test
    void updateTaskDependency_shouldThrowExceptionIfDependencyNotFound() {
        TaskDependency updatedDependency = new TaskDependency();
        updatedDependency.setId(new TaskDependency.TaskDependencyId(999L, 999L));

        given(taskDependencyRepository.findById(updatedDependency.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskDependencyService.updateTaskDependency(updatedDependency))
                .isInstanceOf(TaskDependencyNotFoundException.class)
                .hasMessageContaining("Task dependency not found");
        verify(taskDependencyRepository, never()).save(any());
    }

    @Test
    void deleteTaskDependency_shouldDeleteDependencySuccessfully() {
        given(taskDependencyRepository.existsById(dependency12.getId())).willReturn(true);

        taskDependencyService.deleteTaskDependency(dependency12.getId());

        verify(taskDependencyRepository).deleteById(dependency12.getId());
    }

    @Test
    void deleteTaskDependency_shouldThrowExceptionIfDependencyNotFound() {
        TaskDependency.TaskDependencyId nonExistentId = new TaskDependency.TaskDependencyId(999L, 999L);
        given(taskDependencyRepository.existsById(nonExistentId)).willReturn(false);

        assertThatThrownBy(() -> taskDependencyService.deleteTaskDependency(nonExistentId))
                .isInstanceOf(TaskDependencyNotFoundException.class)
                .hasMessageContaining("Task dependency not found");
        verify(taskDependencyRepository, never()).deleteById(any());
    }

    @Test
    void existsTaskDependency_shouldReturnTrueIfDependencyExists() {
        given(taskDependencyRepository.existsByIdTaskIdAndIdDependsOnTaskId(task1.getId(), task2.getId())).willReturn(true);

        boolean exists = taskDependencyService.existsTaskDependency(task1.getId(), task2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void countDependenciesForTask_shouldReturnCountOfDependencies() {
        given(taskDependencyRepository.countByIdTaskId(task1.getId())).willReturn(2L);

        long count = taskDependencyService.countDependenciesForTask(task1.getId());

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void countDependentsForTask_shouldReturnCountOfDependents() {
        given(taskDependencyRepository.countByIdDependsOnTaskId(task2.getId())).willReturn(3L);

        long count = taskDependencyService.countDependentsForTask(task2.getId());

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void findDirectDependenciesForTask_shouldReturnDirectDependencies() {
        given(taskDependencyRepository.findDirectDependenciesForTask(task1.getId())).willReturn(Arrays.asList(task2));

        List<Task> dependencies = taskDependencyService.findDirectDependenciesForTask(task1.getId());

        assertThat(dependencies).containsExactly(task2);
    }

    @Test
    void findDirectDependentsForTask_shouldReturnDirectDependents() {
        given(taskDependencyRepository.findDirectDependentsForTask(task2.getId())).willReturn(Arrays.asList(task1));

        List<Task> dependents = taskDependencyService.findDirectDependentsForTask(task2.getId());

        assertThat(dependents).containsExactly(task1);
    }
}