package com.daaeboul.taskmanagementsystem.service.task;


import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.DuplicateTaskPriorityNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.TaskPriorityNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.repository.task.TaskPriorityRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskPriorityServiceTest {

    @Mock
    private TaskPriorityRepository taskPriorityRepository;

    @InjectMocks
    private TaskPriorityService taskPriorityService;

    private TaskPriority taskPriority1;
    private TaskPriority taskPriority2;
    private TaskPriority taskPriority3;

    @BeforeEach
    void setUp() {
        taskPriority1 = new TaskPriority();
        taskPriority1.setPriorityName("High");

        taskPriority2 = new TaskPriority();
        taskPriority2.setPriorityName("Medium");

        taskPriority3 = new TaskPriority();
        taskPriority3.setPriorityName("Low");

        ReflectionTestUtils.setField(taskPriority1, "id", 1L);
        ReflectionTestUtils.setField(taskPriority2, "id", 2L);
        ReflectionTestUtils.setField(taskPriority3, "id", 3L);
    }

    @Test
    void createTaskPriority_shouldCreateTaskPrioritySuccessfully() {
        given(taskPriorityRepository.existsByPriorityNameIgnoreCase(taskPriority1.getPriorityName())).willReturn(false);
        given(taskPriorityRepository.save(taskPriority1)).willReturn(taskPriority1);

        TaskPriority createdTaskPriority = taskPriorityService.createTaskPriority(taskPriority1);

        assertThat(createdTaskPriority).isEqualTo(taskPriority1);
        verify(taskPriorityRepository).save(taskPriority1);
    }

    @Test
    void createTaskPriority_shouldThrowExceptionForDuplicateTaskPriorityName() {
        given(taskPriorityRepository.existsByPriorityNameIgnoreCase(taskPriority1.getPriorityName())).willReturn(true);

        assertThatThrownBy(() -> taskPriorityService.createTaskPriority(taskPriority1))
                .isInstanceOf(DuplicateTaskPriorityNameException.class)
                .hasMessageContaining("Task priority name already exists");
        verify(taskPriorityRepository, never()).save(any());
    }

    @Test
    void findTaskPriorityById_shouldReturnTaskPriorityIfFound() {
        given(taskPriorityRepository.findById(taskPriority1.getId())).willReturn(Optional.of(taskPriority1));

        Optional<TaskPriority> foundTaskPriority = taskPriorityService.findTaskPriorityById(taskPriority1.getId());

        assertThat(foundTaskPriority).isPresent().contains(taskPriority1);
    }

    @Test
    void findTaskPriorityById_shouldReturnEmptyOptionalIfNotFound() {
        given(taskPriorityRepository.findById(100L)).willReturn(Optional.empty());

        Optional<TaskPriority> foundTaskPriority = taskPriorityService.findTaskPriorityById(100L);

        assertThat(foundTaskPriority).isEmpty();
    }

    @Test
    void findTaskPriorityByName_shouldReturnTaskPriorityIfFound() {
        given(taskPriorityRepository.findByPriorityNameIgnoreCase(taskPriority1.getPriorityName())).willReturn(Optional.of(taskPriority1));

        Optional<TaskPriority> foundTaskPriority = taskPriorityService.findTaskPriorityByName(taskPriority1.getPriorityName());

        assertThat(foundTaskPriority).isPresent().contains(taskPriority1);
    }

    @Test
    void findTaskPriorityByName_shouldReturnEmptyOptionalIfNotFound() {
        given(taskPriorityRepository.findByPriorityNameIgnoreCase("Nonexistent")).willReturn(Optional.empty());

        Optional<TaskPriority> foundTaskPriority = taskPriorityService.findTaskPriorityByName("Nonexistent");

        assertThat(foundTaskPriority).isEmpty();
    }

    @Test
    void findAllTaskPriorities_shouldReturnAllTaskPriorities() {
        given(taskPriorityRepository.findAll()).willReturn(List.of(taskPriority1, taskPriority2, taskPriority3));

        List<TaskPriority> allTaskPriorities = taskPriorityService.findAllTaskPriorities();

        assertThat(allTaskPriorities).containsExactly(taskPriority1, taskPriority2, taskPriority3);
    }

    @Test
    void findAllTaskPriorities_withPageable_shouldReturnPagedTaskPriorities() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<TaskPriority> pagedTaskPriorities = new PageImpl<>(List.of(taskPriority1, taskPriority2), pageable, 3);
        given(taskPriorityRepository.findAll(pageable)).willReturn(pagedTaskPriorities);

        Page<TaskPriority> result = taskPriorityService.findAllTaskPriorities(pageable);

        assertThat(result).isEqualTo(pagedTaskPriorities);
    }

    @Test
    void findTaskPrioritiesByNameContaining_shouldReturnMatchingTaskPriorities() {
        given(taskPriorityRepository.findByPriorityNameContainingIgnoreCase("med")).willReturn(List.of(taskPriority2));

        List<TaskPriority> result = taskPriorityService.findTaskPrioritiesByNameContaining("med");

        assertThat(result).containsExactly(taskPriority2);
    }

    @Test
    void updateTaskPriority_shouldUpdateTaskPrioritySuccessfully() {
        TaskPriority updatedTaskPriority = new TaskPriority();
        updatedTaskPriority.setPriorityName("Critical");
        ReflectionTestUtils.setField(updatedTaskPriority, "id", taskPriority1.getId());

        given(taskPriorityRepository.findById(taskPriority1.getId())).willReturn(Optional.of(taskPriority1));
        given(taskPriorityRepository.existsByPriorityNameIgnoreCase(updatedTaskPriority.getPriorityName())).willReturn(false); // No duplicate name
        given(taskPriorityRepository.save(any(TaskPriority.class))).willReturn(updatedTaskPriority);

        TaskPriority result = taskPriorityService.updateTaskPriority(updatedTaskPriority);

        assertThat(result).isEqualTo(updatedTaskPriority);
        verify(taskPriorityRepository).save(any(TaskPriority.class));
    }

    @Test
    void updateTaskPriority_shouldThrowExceptionIfTaskPriorityNotFound() {
        TaskPriority updatedTaskPriority = new TaskPriority();
        updatedTaskPriority.setPriorityName("Critical");
        ReflectionTestUtils.setField(updatedTaskPriority, "id", 100L);

        given(taskPriorityRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskPriorityService.updateTaskPriority(updatedTaskPriority))
                .isInstanceOf(TaskPriorityNotFoundException.class)
                .hasMessageContaining("Task priority not found");
        verify(taskPriorityRepository, never()).save(any());
    }

    @Test
    void updateTaskPriority_shouldThrowExceptionForDuplicateTaskPriorityName() {
        TaskPriority updatedTaskPriority = new TaskPriority();
        updatedTaskPriority.setPriorityName(taskPriority2.getPriorityName()); // Duplicate name
        ReflectionTestUtils.setField(updatedTaskPriority, "id", taskPriority1.getId());

        given(taskPriorityRepository.findById(taskPriority1.getId())).willReturn(Optional.of(taskPriority1));
        given(taskPriorityRepository.existsByPriorityNameIgnoreCase(updatedTaskPriority.getPriorityName())).willReturn(true);

        assertThatThrownBy(() -> taskPriorityService.updateTaskPriority(updatedTaskPriority))
                .isInstanceOf(DuplicateTaskPriorityNameException.class)
                .hasMessageContaining("Task priority name already exists");
        verify(taskPriorityRepository, never()).save(any());
    }

    @Test
    void deleteTaskPriority_shouldDeleteTaskPrioritySuccessfully() {
        given(taskPriorityRepository.existsById(taskPriority1.getId())).willReturn(true);

        taskPriorityService.deleteTaskPriority(taskPriority1.getId());

        verify(taskPriorityRepository).deleteById(taskPriority1.getId());
    }

    @Test
    void deleteTaskPriority_shouldThrowExceptionIfTaskPriorityNotFound() {
        given(taskPriorityRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> taskPriorityService.deleteTaskPriority(100L))
                .isInstanceOf(TaskPriorityNotFoundException.class)
                .hasMessageContaining("Task priority not found");
        verify(taskPriorityRepository, never()).deleteById(any());
    }
}