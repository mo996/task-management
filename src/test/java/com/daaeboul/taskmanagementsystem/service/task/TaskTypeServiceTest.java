package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.DuplicateTaskTypeNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.TaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.repository.task.TaskTypeRepository;
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
class TaskTypeServiceTest {

    @Mock
    private TaskTypeRepository taskTypeRepository;

    @InjectMocks
    private TaskTypeService taskTypeService;

    private TaskType taskType1;
    private TaskType taskType2;
    private TaskType taskType3;

    @BeforeEach
    void setUp() {
        taskType1 = new TaskType();
        taskType1.setTaskTypeName("Development");
        taskType1.setDescription("Tasks related to software development projects.");

        taskType2 = new TaskType();
        taskType2.setTaskTypeName("Project Management");
        taskType2.setDescription("Tasks related to managing projects.");

        taskType3 = new TaskType();
        taskType3.setTaskTypeName("Bug Tracking");
        taskType3.setDescription("Tasks related to identifying and fixing bugs.");

        // Set IDs using reflection
        ReflectionTestUtils.setField(taskType1, "id", 1L);
        ReflectionTestUtils.setField(taskType2, "id", 2L);
        ReflectionTestUtils.setField(taskType3, "id", 3L);
    }

    @Test
    void createTaskType_shouldCreateTaskTypeSuccessfully() {
        given(taskTypeRepository.existsByTaskTypeNameIgnoreCase(taskType1.getTaskTypeName())).willReturn(false);
        given(taskTypeRepository.save(taskType1)).willReturn(taskType1);

        TaskType createdTaskType = taskTypeService.createTaskType(taskType1);

        assertThat(createdTaskType).isEqualTo(taskType1);
        verify(taskTypeRepository).save(taskType1);
    }

    @Test
    void createTaskType_shouldThrowExceptionForDuplicateTaskTypeName() {
        given(taskTypeRepository.existsByTaskTypeNameIgnoreCase(taskType1.getTaskTypeName())).willReturn(true);

        assertThatThrownBy(() -> taskTypeService.createTaskType(taskType1))
                .isInstanceOf(DuplicateTaskTypeNameException.class)
                .hasMessageContaining("Task type name already exists");
        verify(taskTypeRepository, never()).save(any());
    }

    @Test
    void findTaskTypeById_shouldReturnTaskTypeIfFound() {
        given(taskTypeRepository.findById(taskType1.getId())).willReturn(Optional.of(taskType1));

        Optional<TaskType> foundTaskType = taskTypeService.findTaskTypeById(taskType1.getId());

        assertThat(foundTaskType).isPresent().contains(taskType1);
    }

    @Test
    void findTaskTypeById_shouldReturnEmptyOptionalIfNotFound() {
        given(taskTypeRepository.findById(100L)).willReturn(Optional.empty());

        Optional<TaskType> foundTaskType = taskTypeService.findTaskTypeById(100L);

        assertThat(foundTaskType).isEmpty();
    }

    @Test
    void findTaskTypeByName_shouldReturnTaskTypeIfFound() {
        given(taskTypeRepository.findByTaskTypeNameIgnoreCase(taskType1.getTaskTypeName())).willReturn(Optional.of(taskType1));

        Optional<TaskType> foundTaskType = taskTypeService.findTaskTypeByName(taskType1.getTaskTypeName());

        assertThat(foundTaskType).isPresent().contains(taskType1);
    }

    @Test
    void findTaskTypeByName_shouldReturnEmptyOptionalIfNotFound() {
        given(taskTypeRepository.findByTaskTypeNameIgnoreCase("Nonexistent")).willReturn(Optional.empty());

        Optional<TaskType> foundTaskType = taskTypeService.findTaskTypeByName("Nonexistent");

        assertThat(foundTaskType).isEmpty();
    }

    @Test
    void findAllTaskTypes_shouldReturnAllTaskTypes() {
        given(taskTypeRepository.findAll()).willReturn(List.of(taskType1, taskType2, taskType3));

        List<TaskType> allTaskTypes = taskTypeService.findAllTaskTypes();

        assertThat(allTaskTypes).containsExactly(taskType1, taskType2, taskType3);
    }

    @Test
    void findAllTaskTypes_withPageable_shouldReturnPagedTaskTypes() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<TaskType> pagedTaskTypes = new PageImpl<>(List.of(taskType1, taskType2), pageable, 3);
        given(taskTypeRepository.findAll(pageable)).willReturn(pagedTaskTypes);

        Page<TaskType> result = taskTypeService.findAllTaskTypes(pageable);

        assertThat(result).isEqualTo(pagedTaskTypes);
    }

    @Test
    void findTaskTypesByNameContaining_shouldReturnMatchingTaskTypes() {
        given(taskTypeRepository.findByTaskTypeNameContainingIgnoreCase("manage")).willReturn(List.of(taskType2));

        List<TaskType> result = taskTypeService.findTaskTypesByNameContaining("manage");

        assertThat(result).containsExactly(taskType2);
    }

    @Test
    void findTaskTypesByDescriptionContaining_shouldReturnMatchingTaskTypes() {
        given(taskTypeRepository.findByDescriptionContainingIgnoreCase("project")).willReturn(List.of(taskType1, taskType2));

        List<TaskType> result = taskTypeService.findTaskTypesByDescriptionContaining("project");

        assertThat(result).containsExactly(taskType1, taskType2);
    }

    @Test
    void updateTaskType_shouldUpdateTaskTypeSuccessfully() {
        TaskType updatedTaskType = new TaskType();
        updatedTaskType.setTaskTypeName("Updated Task Type");
        updatedTaskType.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedTaskType, "id", taskType1.getId());

        given(taskTypeRepository.findById(taskType1.getId())).willReturn(Optional.of(taskType1));
        given(taskTypeRepository.existsByTaskTypeNameIgnoreCase(updatedTaskType.getTaskTypeName())).willReturn(false); // No duplicate name
        given(taskTypeRepository.save(any(TaskType.class))).willReturn(updatedTaskType);

        TaskType result = taskTypeService.updateTaskType(updatedTaskType);

        assertThat(result).isEqualTo(updatedTaskType);
        verify(taskTypeRepository).save(any(TaskType.class));
    }

    @Test
    void updateTaskType_shouldThrowExceptionIfTaskTypeNotFound() {
        TaskType updatedTaskType = new TaskType();
        updatedTaskType.setTaskTypeName("Updated Task Type");
        updatedTaskType.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedTaskType, "id", 100L);

        given(taskTypeRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskTypeService.updateTaskType(updatedTaskType))
                .isInstanceOf(TaskTypeNotFoundException.class)
                .hasMessageContaining("Task type not found");
        verify(taskTypeRepository, never()).save(any());
    }

    @Test
    void updateTaskType_shouldThrowExceptionForDuplicateTaskTypeName() {
        TaskType updatedTaskType = new TaskType();
        updatedTaskType.setTaskTypeName(taskType2.getTaskTypeName()); // Duplicate name
        updatedTaskType.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedTaskType, "id", taskType1.getId());

        given(taskTypeRepository.findById(taskType1.getId())).willReturn(Optional.of(taskType1));
        given(taskTypeRepository.existsByTaskTypeNameIgnoreCase(updatedTaskType.getTaskTypeName())).willReturn(true);

        assertThatThrownBy(() -> taskTypeService.updateTaskType(updatedTaskType))
                .isInstanceOf(DuplicateTaskTypeNameException.class)
                .hasMessageContaining("Task type name already exists");
        verify(taskTypeRepository, never()).save(any());
    }

    @Test
    void deleteTaskType_shouldDeleteTaskTypeSuccessfully() {
        given(taskTypeRepository.existsById(taskType1.getId())).willReturn(true);

        taskTypeService.deleteTaskType(taskType1.getId());

        verify(taskTypeRepository).deleteById(taskType1.getId());
    }

    @Test
    void deleteTaskType_shouldThrowExceptionIfTaskTypeNotFound() {
        given(taskTypeRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> taskTypeService.deleteTaskType(100L))
                .isInstanceOf(TaskTypeNotFoundException.class)
                .hasMessageContaining("Task type not found");
        verify(taskTypeRepository, never()).deleteById(any());
    }
}