package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCommentRepository extends BaseSoftDeletableRepository<TaskComment, Long> {

    /**
     * Finds all task comments for a given task.
     *
     * @param task The task to find comments for.
     * @return A list of task comments for the given task.
     */
    List<TaskComment> findByTask(Task task);

    /**
     * Finds all task comments for a given task, with pagination and sorting.
     *
     * @param task     The task to find comments for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments for the given task.
     */
    Page<TaskComment> findByTask(Task task, Pageable pageable);

    /**
     * Finds a task comment by its ID.
     *
     * @param id The ID of the task comment.
     * @return An Optional containing the task comment if found, otherwise empty.
     */
    @NonNull Optional<TaskComment> findById(@NonNull Long id);

    /**
     * Finds all task comments made by a specific user.
     *
     * @param user The user to find comments for.
     * @return A list of task comments made by the given user.
     */
    List<TaskComment> findByUser(User user);

    /**
     * Finds all task comments made by a specific user, with pagination and sorting.
     *
     * @param user     The user to find comments for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments made by the given user.
     */
    Page<TaskComment> findByUser(User user, Pageable pageable);

    /**
     * Finds task comments that contain a specific keyword in the comment text (case-insensitive).
     *
     * @param keyword The keyword to search for.
     * @return A list of task comments containing the keyword.
     */
    List<TaskComment> findByCommentContainingIgnoreCase(String keyword);

    /**
     * Finds task comments that contain a specific keyword in the comment text (case-insensitive), with pagination and sorting.
     *
     * @param keyword  The keyword to search for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments containing the keyword.
     */
    Page<TaskComment> findByCommentContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Checks if a task comment exists with the given ID.
     *
     * @param id The ID of the task comment.
     * @return True if a task comment with the given ID exists, false otherwise.
     */
    boolean existsById(@NonNull Long id);

    /**
     * Finds all task comments, including deleted ones.
     *
     * @return A list of all task comments, including deleted ones.
     */
    @Query("SELECT tc FROM TaskComment tc")
    List<TaskComment> findAllIncludingDeleted();

    /**
     * Finds all task comments for a given task, including deleted ones.
     *
     * @param taskId The ID of the task.
     * @return A list of all task comments for the given task, including deleted ones.
     */
    @Query("SELECT tc FROM TaskComment tc WHERE tc.task.id = :taskId")
    List<TaskComment> findAllByTaskIdIncludingDeleted(@Param("taskId") Long taskId);
}