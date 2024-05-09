package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.DuplicateTaskTypeNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.TaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.repository.task.TaskTypeRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    @Autowired
    public TaskTypeService(TaskTypeRepository taskTypeRepository) {
        this.taskTypeRepository = taskTypeRepository;
    }

    /**
     * Creates a new task type.
     *
     * @param taskType The task type to create.
     * @return The created task type.
     * @throws DuplicateTaskTypeNameException If a task type with the same name already exists (case-insensitive).
     */
    @Transactional
    public TaskType createTaskType(TaskType taskType) {
        if (taskTypeRepository.existsByTaskTypeNameIgnoreCase(taskType.getTaskTypeName())) {
            throw new DuplicateTaskTypeNameException("Task type name already exists: " + taskType.getTaskTypeName());
        }
        return taskTypeRepository.save(taskType);
    }

    /**
     * Finds a task type by its ID.
     *
     * @param id The ID of the task type.
     * @return An Optional containing the task type if found, otherwise empty.
     */
    public Optional<TaskType> findTaskTypeById(Long id) {
        return taskTypeRepository.findById(id);
    }

    /**
     * Finds a task type by its name (case-insensitive).
     *
     * @param taskTypeName The name of the task type to search for.
     * @return An Optional containing the task type if found, otherwise empty.
     */
    public Optional<TaskType> findTaskTypeByName(String taskTypeName) {
        return taskTypeRepository.findByTaskTypeNameIgnoreCase(taskTypeName);
    }

    /**
     * Finds all task types.
     *
     * @return A list of all task types.
     */
    public List<TaskType> findAllTaskTypes() {
        return taskTypeRepository.findAll();
    }

    /**
     * Finds all task types with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of task type entities.
     */
    public @NonNull Page<TaskType> findAllTaskTypes(@NonNull Pageable pageable) {
        return taskTypeRepository.findAll(pageable);
    }

    /**
     * Finds task types whose names contain the given string (case-insensitive).
     *
     * @param namePart The part of the task type name to search for.
     * @return A list of task type entities that match the given name part.
     */
    public List<TaskType> findTaskTypesByNameContaining(String namePart) {
        return taskTypeRepository.findByTaskTypeNameContainingIgnoreCase(namePart);
    }

    /**
     * Finds task types by a part of their description (case-insensitive).
     *
     * @param descriptionPart The part of the description to search for.
     * @return A list of task type entities whose descriptions contain the given part.
     */
    public List<TaskType> findTaskTypesByDescriptionContaining(String descriptionPart) {
        return taskTypeRepository.findByDescriptionContainingIgnoreCase(descriptionPart);
    }

    /**
     * Updates a task type.
     *
     * @param updatedTaskType The task type with updated information.
     * @return The updated task type.
     * @throws TaskTypeNotFoundException    If the task type is not found.
     * @throws DuplicateTaskTypeNameException If a task type with the same name already exists (case-insensitive).
     */
    @Transactional
    public TaskType updateTaskType(TaskType updatedTaskType) {
        TaskType existingTaskType = taskTypeRepository.findById(updatedTaskType.getId())
                .orElseThrow(() -> new TaskTypeNotFoundException("Task type not found with ID: " + updatedTaskType.getId()));

        // Check for duplicate task type name before updating
        if (taskTypeRepository.existsByTaskTypeNameIgnoreCase(updatedTaskType.getTaskTypeName()) &&
                !existingTaskType.getTaskTypeName().equalsIgnoreCase(updatedTaskType.getTaskTypeName())) {
            throw new DuplicateTaskTypeNameException("Task type name already exists: " + updatedTaskType.getTaskTypeName());
        }

        // Update fields
        existingTaskType.setTaskTypeName(updatedTaskType.getTaskTypeName());
        existingTaskType.setDescription(updatedTaskType.getDescription());

        return taskTypeRepository.save(existingTaskType);
    }

    /**
     * Deletes a task type by its ID.
     *
     * @param id The ID of the task type to delete.
     * @throws TaskTypeNotFoundException If the task type is not found.
     */
    @Transactional
    public void deleteTaskType(Long id) {
        if (!taskTypeRepository.existsById(id)) {
            throw new TaskTypeNotFoundException("Task type not found with ID: " + id);
        }
        taskTypeRepository.deleteById(id);
    }
}
