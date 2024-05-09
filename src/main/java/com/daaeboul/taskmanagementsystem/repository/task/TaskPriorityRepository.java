package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {

    /**
     * Finds a task priority by its name (case-insensitive).
     *
     * @param priorityName The name of the task priority to search for.
     * @return An Optional containing the TaskPriority entity if found, otherwise empty.
     */
    Optional<TaskPriority> findByPriorityNameIgnoreCase(String priorityName);

    /**
     * Finds task priorities whose names contain the given string (case-insensitive).
     * This is useful for searching or filtering task priorities.
     *
     * @param namePart The part of the task priority name to search for.
     * @return A list of TaskPriority entities that match the given name part.
     */
    List<TaskPriority> findByPriorityNameContainingIgnoreCase(String namePart);

    /**
     * Finds all task priorities with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of TaskPriority entities.
     */
    @NonNull Page<TaskPriority> findAll(@NonNull Pageable pageable);

    /**
     * Checks if a task priority exists with the given name (case-insensitive).
     * This is useful for validation during task priority creation or updating.
     *
     * @param priorityName The name of the task priority to check.
     * @return True if a task priority with the given name exists, false otherwise.
     */
    boolean existsByPriorityNameIgnoreCase(String priorityName);

}
