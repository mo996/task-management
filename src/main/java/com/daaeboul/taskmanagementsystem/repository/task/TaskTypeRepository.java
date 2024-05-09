package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

    /**
     * Finds a task type by its name (case-insensitive).
     *
     * @param taskTypeName The name of the task type to search for.
     * @return An Optional containing the TaskType entity if found, otherwise empty.
     */
    Optional<TaskType> findByTaskTypeNameIgnoreCase(String taskTypeName);

    /**
     * Finds task types whose names contain the given string (case-insensitive).
     * This is useful for searching or filtering task types.
     *
     * @param namePart The part of the task type name to search for.
     * @return A list of TaskType entities that match the given name part.
     */
    List<TaskType> findByTaskTypeNameContainingIgnoreCase(String namePart);

    /**
     * Finds all task types with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of TaskType entities.
     */
    @NonNull Page<TaskType> findAll(@NonNull Pageable pageable);

    /**
     * Finds task types by a part of their description (case-insensitive).
     *
     * @param descriptionPart The part of the description to search for.
     * @return A list of TaskType entities whose descriptions contain the given part.
     */
    List<TaskType> findByDescriptionContainingIgnoreCase(String descriptionPart);

    /**
     * Checks if a task type exists with the given name (case-insensitive).
     * This is useful for validation during task type creation or updating.
     *
     * @param taskTypeName The name of the task type to check.
     * @return True if a task type with the given name exists, false otherwise.
     */
    boolean existsByTaskTypeNameIgnoreCase(String taskTypeName);
}
