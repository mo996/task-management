package com.daaeboul.taskmanagementsystem.repository.task;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency.TaskDependencyId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, TaskDependencyId> {

    /**
     * Finds task dependencies by the ID of the task that has the dependency.
     * This helps in identifying all tasks that a specific task depends on.
     *
     * @param taskId the ID of the task with the dependency
     * @return a list of TaskDependency entities
     */
    List<TaskDependency> findByIdTaskId(Long taskId);

    /**
     * Finds task dependencies with pagination and sorting based on the ID of the task that has the dependency.
     *
     * @param taskId   the ID of the task with the dependency
     * @param pageable pagination and sorting parameters
     * @return a Page of TaskDependency entities
     */
    Page<TaskDependency> findByIdTaskId(Long taskId, Pageable pageable);

    /**
     * Finds task dependencies by the ID of the task that the first task depends on.
     * This helps in identifying all tasks that depend on a specific task.
     *
     * @param dependsOnTaskId the ID of the task that the first task depends on
     * @return a list of TaskDependency entities
     */
    List<TaskDependency> findByIdDependsOnTaskId(Long dependsOnTaskId);

    /**
     * Finds a specific task dependency by its composite primary key.
     *
     * @param taskId         the ID of the task with the dependency
     * @param dependsOnTaskId the ID of the task that the first task depends on
     * @return an Optional containing the TaskDependency entity if found, otherwise empty
     */
    Optional<TaskDependency> findByIdTaskIdAndIdDependsOnTaskId(Long taskId, Long dependsOnTaskId);

    /**
     * Checks if a dependency exists between two tasks.
     *
     * @param taskId         the ID of the task with the dependency
     * @param dependsOnTaskId the ID of the task that the first task depends on
     * @return true if the dependency exists, false otherwise
     */
    boolean existsByIdTaskIdAndIdDependsOnTaskId(Long taskId, Long dependsOnTaskId);

    /**
     * Counts the number of dependencies for a given task (where the task has the dependency).
     *
     * @param taskId the ID of the task with the dependency
     * @return the count of dependencies
     */
    long countByIdTaskId(Long taskId);

    /**
     * Counts the number of tasks that depend on a given task (where the given task is the blocker).
     *
     * @param dependsOnTaskId the ID of the task that other tasks depend on
     * @return the count of dependent tasks
     */
    long countByIdDependsOnTaskId(Long dependsOnTaskId);

    /**
     * Finds tasks that a given task directly depends on.
     *
     * @param taskId the ID of the task that has dependencies
     * @return a list of tasks that the given task depends on
     */
    @Query("SELECT td.dependsOnTask FROM TaskDependency td WHERE td.task.id = :taskId")
    List<Task> findDirectDependenciesForTask(@Param("taskId") Long taskId);

    /**
     * Finds tasks that directly depend on a given task.
     *
     * @param taskId the ID of the task that other tasks depend on
     * @return a list of tasks that depend on the given task
     */
    @Query("SELECT td.task FROM TaskDependency td WHERE td.dependsOnTask.id = :taskId")
    List<Task> findDirectDependentsForTask(@Param("taskId") Long taskId);
}