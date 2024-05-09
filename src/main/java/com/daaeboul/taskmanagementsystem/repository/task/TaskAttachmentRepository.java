package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    /**
     * Finds task attachments associated with a specific task ID.
     *
     * @param taskId The ID of the task.
     * @return A list of TaskAttachment entities associated with the given task ID.
     */
    List<TaskAttachment> findByTaskId(Long taskId);

    /**
     * Finds a task attachment by its ID.
     *
     * @param id The ID of the task attachment.
     * @return An Optional containing the TaskAttachment entity if found, otherwise empty.
     */
    @NonNull Optional<TaskAttachment> findById(@NonNull Long id);

    /**
     * Finds task attachments by file name (case-insensitive).
     *
     * @param fileName The file name to search for.
     * @return A list of TaskAttachment entities with the matching file name.
     */
    List<TaskAttachment> findByFileNameIgnoreCase(String fileName);

    /**
     * Finds task attachments by file type (case-insensitive).
     *
     * @param fileType The file type to search for.
     * @return A list of TaskAttachment entities with the matching file type.
     */
    List<TaskAttachment> findByFileTypeIgnoreCase(String fileType);

    /**
     * Checks if a task attachment exists with the given ID.
     *
     * @param id The ID of the task attachment.
     * @return True if a task attachment with the given ID exists, false otherwise.
     */
    boolean existsById(@NonNull Long id);
}
