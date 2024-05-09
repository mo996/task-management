package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskDependendy.TaskDependencyNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.repository.task.TaskDependencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskDependencyService {

    private final TaskDependencyRepository taskDependencyRepository;

    @Autowired
    public TaskDependencyService(TaskDependencyRepository taskDependencyRepository) {
        this.taskDependencyRepository = taskDependencyRepository;
    }

    /**
     * Creates a new task dependency.
     *
     * @param taskDependency The task dependency to create.
     * @return The created task dependency.
     */
    @Transactional
    public TaskDependency createTaskDependency(TaskDependency taskDependency) {
        return taskDependencyRepository.save(taskDependency);
    }

    /**
     * Finds a task dependency by its composite ID.
     *
     * @param id The composite ID of the task dependency.
     * @return An Optional containing the task dependency if found, otherwise empty.
     */
    public Optional<TaskDependency> findTaskDependencyById(TaskDependency.TaskDependencyId id) {
        return taskDependencyRepository.findById(id);
    }

    /**
     * Finds all task dependencies for a given task ID (the task that has the dependency).
     *
     * @param taskId The ID of the task with dependencies.
     * @return A list of task dependencies for the given task.
     */
    public List<TaskDependency> findTaskDependenciesByTaskId(Long taskId) {
        return taskDependencyRepository.findByIdTaskId(taskId);
    }

    /**
     * Finds all task dependencies for a given task ID (the task that has the dependency), with pagination and sorting.
     *
     * @param taskId   The ID of the task with dependencies.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task dependencies for the given task.
     */
    public Page<TaskDependency> findTaskDependenciesByTaskId(Long taskId, Pageable pageable) {
        return taskDependencyRepository.findByIdTaskId(taskId, pageable);
    }

    /**
     * Finds all task dependencies for a given dependsOnTask ID (the task that is blocking other tasks).
     *
     * @param dependsOnTaskId The ID of the task that other tasks depend on.
     * @return A list of task dependencies where the given task is the blocker.
     */
    public List<TaskDependency> findTaskDependenciesByDependsOnTaskId(Long dependsOnTaskId) {
        return taskDependencyRepository.findByIdDependsOnTaskId(dependsOnTaskId);
    }

    /**
     * Finds a specific task dependency by the IDs of the involved tasks.
     *
     * @param taskId         The ID of the task that has the dependency.
     * @param dependsOnTaskId The ID of the task that the first task depends on.
     * @return An Optional containing the task dependency if found, otherwise empty.
     */
    public Optional<TaskDependency> findTaskDependencyByTaskIds(Long taskId, Long dependsOnTaskId) {
        return taskDependencyRepository.findByIdTaskIdAndIdDependsOnTaskId(taskId, dependsOnTaskId);
    }

    /**
     * Updates a task dependency.
     *
     * @param updatedTaskDependency The task dependency with updated information.
     * @return The updated task dependency.
     * @throws TaskDependencyNotFoundException If the task dependency is not found.
     */
    @Transactional
    public TaskDependency updateTaskDependency(TaskDependency updatedTaskDependency) {
        TaskDependency existingTaskDependency = taskDependencyRepository.findById(updatedTaskDependency.getId())
                .orElseThrow(() -> new TaskDependencyNotFoundException("Task dependency not found with ID: " + updatedTaskDependency.getId()));

        existingTaskDependency.setId(updatedTaskDependency.getId());
        existingTaskDependency.setTask(updatedTaskDependency.getTask());
        existingTaskDependency.setDependsOnTask(updatedTaskDependency.getDependsOnTask());

        return taskDependencyRepository.save(existingTaskDependency);
    }

    /**
     * Deletes a task dependency by its composite ID.
     *
     * @param id The composite ID of the task dependency to delete.
     * @throws TaskDependencyNotFoundException If the task dependency is not found.
     */
    @Transactional
    public void deleteTaskDependency(TaskDependency.TaskDependencyId id) {
        if (!taskDependencyRepository.existsById(id)) {
            throw new TaskDependencyNotFoundException("Task dependency not found with ID: " + id);
        }
        taskDependencyRepository.deleteById(id);
    }

    /**
     * Checks if a dependency exists between two tasks.
     *
     * @param taskId The ID of the task that might have the dependency.
     * @param dependsOnTaskId The ID of the task that the first task might depend on.
     * @return True if the dependency exists, false otherwise.
     */
    public boolean existsTaskDependency(Long taskId, Long dependsOnTaskId) {
        return taskDependencyRepository.existsByIdTaskIdAndIdDependsOnTaskId(taskId, dependsOnTaskId);
    }

    /**
     * Counts the number of dependencies for a given task.
     *
     * @param taskId The ID of the task.
     * @return The count of dependencies for the task.
     */
    public long countDependenciesForTask(Long taskId) {
        return taskDependencyRepository.countByIdTaskId(taskId);
    }

    /**
     * Counts the number of tasks that depend on a given task.
     *
     * @param dependsOnTaskId The ID of the task that other tasks might depend on.
     * @return The count of tasks that depend on the given task.
     */
    public long countDependentsForTask(Long dependsOnTaskId) {
        return taskDependencyRepository.countByIdDependsOnTaskId(dependsOnTaskId);
    }

    /**
     * Finds tasks that a given task directly depends on.
     *
     * @param taskId The ID of the task that has dependencies.
     * @return A list of tasks that the given task depends on.
     */
    public List<Task> findDirectDependenciesForTask(Long taskId) {
        return taskDependencyRepository.findDirectDependenciesForTask(taskId);
    }

    /**
     * Finds tasks that directly depend on a given task.
     *
     * @param taskId The ID of the task that other tasks depend on.
     * @return A list of tasks that depend on the given task.
     */
    public List<Task> findDirectDependentsForTask(Long taskId) {
        return taskDependencyRepository.findDirectDependentsForTask(taskId);
    }
}
