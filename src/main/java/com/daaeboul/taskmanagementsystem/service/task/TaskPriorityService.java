package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.DuplicateTaskPriorityNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.TaskPriorityNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.repository.task.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;

    @Autowired
    public TaskPriorityService(TaskPriorityRepository taskPriorityRepository) {
        this.taskPriorityRepository = taskPriorityRepository;
    }

    /**
     * Creates a new task priority.
     *
     * @param taskPriority The task priority to create.
     * @return The created task priority.
     * @throws DuplicateTaskPriorityNameException If a task priority with the same name already exists (case-insensitive).
     */
    @Transactional
    public TaskPriority createTaskPriority(TaskPriority taskPriority) {
        if (taskPriorityRepository.existsByPriorityNameIgnoreCase(taskPriority.getPriorityName())) {
            throw new DuplicateTaskPriorityNameException("Task priority name already exists: " + taskPriority.getPriorityName());
        }
        return taskPriorityRepository.save(taskPriority);
    }

    /**
     * Finds a task priority by its ID.
     *
     * @param id The ID of the task priority.
     * @return An Optional containing the task priority if found, otherwise empty.
     */
    public Optional<TaskPriority> findTaskPriorityById(Long id) {
        return taskPriorityRepository.findById(id);
    }

    /**
     * Finds a task priority by its name (case-insensitive).
     *
     * @param priorityName The name of the task priority to search for.
     * @return An Optional containing the task priority if found, otherwise empty.
     */
    public Optional<TaskPriority> findTaskPriorityByName(String priorityName) {
        return taskPriorityRepository.findByPriorityNameIgnoreCase(priorityName);
    }

    /**
     * Finds all task priorities.
     *
     * @return A list of all task priorities.
     */
    public List<TaskPriority> findAllTaskPriorities() {
        return taskPriorityRepository.findAll();
    }

    /**
     * Finds all task priorities with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of task priority entities.
     */
    public Page<TaskPriority> findAllTaskPriorities(Pageable pageable) {
        return taskPriorityRepository.findAll(pageable);
    }

    /**
     * Finds task priorities whose names contain the given string (case-insensitive).
     *
     * @param namePart The part of the task priority name to search for.
     * @return A list of task priority entities that match the given name part.
     */
    public List<TaskPriority> findTaskPrioritiesByNameContaining(String namePart) {
        return taskPriorityRepository.findByPriorityNameContainingIgnoreCase(namePart);
    }

    /**
     * Updates a task priority.
     *
     * @param updatedTaskPriority The task priority with updated information.
     * @return The updated task priority.
     * @throws TaskPriorityNotFoundException If the task priority is not found.
     * @throws DuplicateTaskPriorityNameException If a task priority with the same name already exists (case-insensitive).
     */
    @Transactional
    public TaskPriority updateTaskPriority(TaskPriority updatedTaskPriority) {
        TaskPriority existingTaskPriority = taskPriorityRepository.findById(updatedTaskPriority.getId())
                .orElseThrow(() -> new TaskPriorityNotFoundException("Task priority not found with ID: " + updatedTaskPriority.getId()));

        if (taskPriorityRepository.existsByPriorityNameIgnoreCase(updatedTaskPriority.getPriorityName()) &&
                !existingTaskPriority.getPriorityName().equalsIgnoreCase(updatedTaskPriority.getPriorityName())) {
            throw new DuplicateTaskPriorityNameException("Task priority name already exists: " + updatedTaskPriority.getPriorityName());
        }

        existingTaskPriority.setPriorityName(updatedTaskPriority.getPriorityName());

        return taskPriorityRepository.save(existingTaskPriority);
    }

    /**
     * Deletes a task priority by its ID.
     *
     * @param id The ID of the task priority to delete.
     * @throws TaskPriorityNotFoundException If the task priority is not found.
     */
    @Transactional
    public void deleteTaskPriority(Long id) {
        if (!taskPriorityRepository.existsById(id)) {
            throw new TaskPriorityNotFoundException("Task priority not found with ID: " + id);
        }
        taskPriorityRepository.deleteById(id);
    }
}
