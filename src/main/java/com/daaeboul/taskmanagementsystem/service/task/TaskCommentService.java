package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskComment.TaskCommentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.task.TaskCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;

    @Autowired
    public TaskCommentService(TaskCommentRepository taskCommentRepository) {
        this.taskCommentRepository = taskCommentRepository;
    }

    /**
     * Creates a new task comment and associates it with a task and user.
     *
     * @param task    The task to which the comment belongs.
     * @param user    The user who made the comment.
     * @param comment The task comment to create.
     * @return The created task comment.
     */
    @Transactional
    public TaskComment createTaskComment(Task task, User user, TaskComment comment) {
        comment.setTask(task);
        comment.setUser(user);
        return taskCommentRepository.save(comment);
    }

    /**
     * Finds a task comment by its ID.
     *
     * @param id The ID of the task comment.
     * @return An Optional containing the task comment if found, otherwise empty.
     */
    public Optional<TaskComment> findTaskCommentById(Long id) {
        return taskCommentRepository.findById(id);
    }

    /**
     * Finds all task comments.
     *
     * @return A list of all task comments.
     */
    public List<TaskComment> findAllTaskComments() {
        return taskCommentRepository.findAll();
    }

    /**
     * Finds all task comments for a given task.
     *
     * @param task The task to find comments for.
     * @return A list of task comments for the given task.
     */
    public List<TaskComment> findTaskCommentsByTask(Task task) {
        return taskCommentRepository.findByTask(task);
    }

    /**
     * Finds all task comments for a given task, with pagination and sorting.
     *
     * @param task     The task to find comments for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments for the given task.
     */
    public Page<TaskComment> findTaskCommentsByTask(Task task, Pageable pageable) {
        return taskCommentRepository.findByTask(task, pageable);
    }

    /**
     * Finds all task comments made by a specific user.
     *
     * @param user The user to find comments for.
     * @return A list of task comments made by the given user.
     */
    public List<TaskComment> findTaskCommentsByUser(User user) {
        return taskCommentRepository.findByUser(user);
    }

    /**
     * Finds all task comments made by a specific user, with pagination and sorting.
     *
     * @param user     The user to find comments for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments made by the given user.
     */
    public Page<TaskComment> findTaskCommentsByUser(User user, Pageable pageable) {
        return taskCommentRepository.findByUser(user, pageable);
    }

    /**
     * Finds task comments that contain a specific keyword in the comment text (case-insensitive).
     *
     * @param keyword The keyword to search for.
     * @return A list of task comments containing the keyword.
     */
    public List<TaskComment> findTaskCommentsByKeyword(String keyword) {
        return taskCommentRepository.findByCommentContainingIgnoreCase(keyword);
    }

    /**
     * Finds task comments that contain a specific keyword in the comment text (case-insensitive), with pagination and sorting.
     *
     * @param keyword  The keyword to search for.
     * @param pageable Pagination and sorting parameters.
     * @return A page of task comments containing the keyword.
     */
    public Page<TaskComment> findTaskCommentsByKeyword(String keyword, Pageable pageable) {
        return taskCommentRepository.findByCommentContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Updates a task comment.
     *
     * @param updatedComment The task comment with updated information.
     * @return The updated task comment.
     * @throws TaskCommentNotFoundException If the task comment is not found.
     */
    @Transactional
    public TaskComment updateTaskComment(TaskComment updatedComment) {
        TaskComment existingComment = taskCommentRepository.findById(updatedComment.getId())
                .orElseThrow(() -> new TaskCommentNotFoundException("Task comment not found with ID: " + updatedComment.getId()));

        existingComment.setComment(updatedComment.getComment());

        return taskCommentRepository.save(existingComment);
    }

    /**
     * Deletes a task comment by its ID.
     *
     * @param id The ID of the task comment to delete.
     * @throws TaskCommentNotFoundException If the task comment is not found.
     */
    @Transactional
    public void deleteTaskComment(Long id) {
        if (!taskCommentRepository.existsById(id)) {
            throw new TaskCommentNotFoundException("Task comment not found with ID: " + id);
        }
        taskCommentRepository.deleteById(id);
    }

    /**
     * Finds all task comments, including deleted ones.
     *
     * @return A list of all task comments, including deleted ones.
     */
    public List<TaskComment> findAllTaskCommentsIncludingDeleted() {
        return taskCommentRepository.findAllIncludingDeleted();
    }

    /**
     * Finds all task comments for a given task, including deleted ones.
     *
     * @param taskId The ID of the task.
     * @return A list of all task comments for the given task, including deleted ones.
     */
    public List<TaskComment> findAllTaskCommentsByTaskIdIncludingDeleted(Long taskId) {
        return taskCommentRepository.findAllByTaskIdIncludingDeleted(taskId);
    }
}
