package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskAttachment.TaskAttachmentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import com.daaeboul.taskmanagementsystem.repository.task.TaskAttachmentRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskAttachmentService {

    private final TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    public TaskAttachmentService(TaskAttachmentRepository taskAttachmentRepository) {
        this.taskAttachmentRepository = taskAttachmentRepository;
    }

    /**
     * Creates a new task attachment and associates it with a task.
     *
     * @param task        The task to which the attachment belongs.
     * @param attachment The task attachment to create.
     * @return The created task attachment.
     */
    @Transactional
    public TaskAttachment createTaskAttachment(Task task, TaskAttachment attachment) {
        attachment.setTask(task);
        return taskAttachmentRepository.save(attachment);
    }

    /**
     * Finds a task attachment by its ID.
     *
     * @param id The ID of the task attachment.
     * @return An Optional containing the task attachment if found, otherwise empty.
     */
    public @NonNull Optional<TaskAttachment> findTaskAttachmentById(@NonNull Long id) {
        return taskAttachmentRepository.findById(id);
    }

    /**
     * Finds all task attachments associated with a specific task.
     *
     * @param taskId The ID of the task.
     * @return A list of task attachments associated with the given task ID.
     */
    public List<TaskAttachment> findTaskAttachmentsByTaskId(Long taskId) {
        return taskAttachmentRepository.findByTaskId(taskId);
    }

    /**
     * Updates a task attachment.
     *
     * @param updatedAttachment The task attachment with updated information.
     * @return The updated task attachment.
     * @throws TaskAttachmentNotFoundException If the task attachment is not found.
     */
    @Transactional
    public TaskAttachment updateTaskAttachment(TaskAttachment updatedAttachment) {
        TaskAttachment existingAttachment = taskAttachmentRepository.findById(updatedAttachment.getId())
                .orElseThrow(() -> new TaskAttachmentNotFoundException("Task attachment not found with ID: " + updatedAttachment.getId()));

        existingAttachment.setFileName(updatedAttachment.getFileName());
        existingAttachment.setFileSize(updatedAttachment.getFileSize());
        existingAttachment.setFileType(updatedAttachment.getFileType());
        existingAttachment.setFileContent(updatedAttachment.getFileContent());

        return taskAttachmentRepository.save(existingAttachment);
    }

    /**
     * Deletes a task attachment by its ID.
     *
     * @param id The ID of the task attachment to delete.
     * @throws TaskAttachmentNotFoundException If the task attachment is not found.
     */
    @Transactional
    public void deleteTaskAttachment(Long id) {
        if (!taskAttachmentRepository.existsById(id)) {
            throw new TaskAttachmentNotFoundException("Task attachment not found with ID: " + id);
        }
        taskAttachmentRepository.deleteById(id);
    }

    /**
     * Finds task attachments by file name (case-insensitive).
     *
     * @param fileName The file name to search for.
     * @return A list of TaskAttachment entities with the matching file name.
     */
    public List<TaskAttachment> findTaskAttachmentsByFileName(String fileName) {
        return taskAttachmentRepository.findByFileNameIgnoreCase(fileName);
    }

    /**
     * Finds task attachments by file type (case-insensitive).
     *
     * @param fileType The file type to search for.
     * @return A list of TaskAttachment entities with the matching file type.
     */
    public List<TaskAttachment> findTaskAttachmentsByFileType(String fileType) {
        return taskAttachmentRepository.findByFileTypeIgnoreCase(fileType);
    }
}