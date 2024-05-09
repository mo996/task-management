package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.task.TaskNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    /**
     * Creates a new task.
     *
     * @param task The task to create.
     * @return The created task.
     */
    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Finds a task by its ID.
     *
     * @param id The ID of the task to find.
     * @return An Optional containing the task if found, otherwise empty.
     */
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Finds all tasks.
     *
     * @return A list of all tasks.
     */
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Updates a task.
     *
     * @param updatedTask The task with updated information.
     * @return The updated task.
     * @throws TaskNotFoundException If the task is not found.
     */
    @Transactional
    public Task updateTask(Task updatedTask) {
        Task existingTask = taskRepository.findById(updatedTask.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + updatedTask.getId()));

        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setTaskDueDate(updatedTask.getTaskDueDate());
        existingTask.setTaskDescription(updatedTask.getTaskDescription());
        existingTask.setTaskTitle(updatedTask.getTaskTitle());
        existingTask.setTaskType(updatedTask.getTaskType());
        existingTask.setAssignee(updatedTask.getAssignee());
        existingTask.setProject(updatedTask.getProject());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setDependencies(updatedTask.getDependencies());
        existingTask.setPrecedencies(updatedTask.getPrecedencies());
        existingTask.setCompletedAt(updatedTask.getCompletedAt());
        existingTask.setDeletedAt(updatedTask.getDeletedAt());

        return taskRepository.save(existingTask);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id The ID of the task to delete.
     * @throws TaskNotFoundException If the task is not found.
     */
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Finds tasks assigned to a specific user.
     *
     * @param assignee The user to whom the tasks are assigned.
     * @return A list of tasks assigned to the specified user.
     */
    public List<Task> findTasksByAssignee(User assignee) {
        return taskRepository.findByAssignee(assignee);
    }

    /**
     * Finds tasks belonging to a specific category.
     *
     * @param category The category of the tasks to search for.
     * @return A list of tasks belonging to the specified category.
     */
    public List<Task> findTasksByCategory(Category category) {
        return taskRepository.findByCategory(category);
    }

    /**
     * Finds tasks with a specific priority.
     *
     * @param priority The priority of the tasks to search for.
     * @return A list of tasks with the specified priority.
     */
    public List<Task> findTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    /**
     * Finds tasks associated with a specific project.
     *
     * @param project The project associated with the tasks to search for.
     * @return A list of tasks associated with the specified project.
     */
    public List<Task> findTasksByProject(Project project) {
        return taskRepository.findByProject(project);
    }

    /**
     * Finds tasks with a specific status.
     *
     * @param status The status of the tasks to search for.
     * @return A list of tasks with the specified status.
     */
    public List<Task> findTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Finds tasks with a due date before a specific date.
     *
     * @param date The date to compare with task due dates.
     * @return A list of tasks with due dates before the specified date.
     */
    public List<Task> findTasksDueBefore(LocalDate date) {
        return taskRepository.findByTaskDueDateBefore(date);
    }

    /**
     * Finds tasks with a due date after a specific date.
     *
     * @param date The date to compare with task due dates.
     * @return A list of tasks with due dates after the specified date.
     */
    public List<Task> findTasksDueAfter(LocalDate date) {
        return taskRepository.findByTaskDueDateAfter(date);
    }

    /**
     * Finds tasks with a due date between two specified dates.
     *
     * @param startDate The start date of the range to compare with task due dates.
     * @param endDate   The end date of the range to compare with task due dates.
     * @return A list of tasks with due dates within the specified range.
     */
    public List<Task> findTasksDueBetween(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByTaskDueDateBetween(startDate, endDate);
    }

    /**
     * Finds tasks that have not yet been completed (completedAt is null).
     *
     * @return A list of incomplete tasks.
     */
    public List<Task> findIncompleteTasks() {
        return taskRepository.findByCompletedAtIsNull();
    }

    /**
     * Finds tasks that have been completed (completedAt is not null).
     *
     * @return A list of completed tasks.
     */
    public List<Task> findCompletedTasks() {
        return taskRepository.findByCompletedAtIsNotNull();
    }

    /**
     * Finds tasks assigned to a specific user and having a particular status.
     *
     * @param assignee The assignee of the tasks to search for.
     * @param status   The status of the tasks to search for.
     * @return A list of tasks matching the given assignee and status criteria.
     */
    public List<Task> findTasksByAssigneeAndStatus(User assignee, Status status) {
        return taskRepository.findTasksByAssigneeAndStatus(assignee, status);
    }

    /**
     * Finds overdue tasks for a specific project.
     *
     * @param project The project for which to find overdue tasks.
     * @param date    The reference date to determine if a task is overdue.
     * @return A list of overdue tasks for the given project.
     */
    public List<Task> findOverdueTasksByProject(Project project, LocalDate date) {
        return taskRepository.findOverdueTasksByProject(project, date);
    }

    /**
     * Finds tasks that have been soft-deleted (deletedAt is not null).
     *
     * @return A list of soft-deleted tasks.
     */
    public List<Task> findAllDeletedTasks() {
        return taskRepository.findAllDeleted();
    }
}
