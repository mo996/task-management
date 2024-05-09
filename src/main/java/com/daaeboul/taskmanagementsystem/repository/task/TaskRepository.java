package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends BaseSoftDeletableRepository<Task, Long> {

    /**
     * Finds tasks assigned to a specific user.
     *
     * @param assignee The user to whom the tasks are assigned.
     * @return A list of tasks assigned to the specified user.
     */
    List<Task> findByAssignee(User assignee);

    /**
     * Finds tasks belonging to a specific category.
     *
     * @param category The category of the tasks to search for.
     * @return A list of tasks belonging to the specified category.
     */
    List<Task> findByCategory(Category category);

    /**
     * Finds tasks with a specific priority.
     *
     * @param priority The priority of the tasks to search for.
     * @return A list of tasks with the specified priority.
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Finds tasks associated with a specific project.
     *
     * @param project The project associated with the tasks to search for.
     * @return A list of tasks associated with the specified project.
     */
    List<Task> findByProject(Project project);

    /**
     * Finds tasks with a specific status.
     *
     * @param status The status of the tasks to search for.
     * @return A list of tasks with the specified status.
     */
    List<Task> findByStatus(Status status);

    /**
     * Finds tasks with a due date before a specific date.
     *
     * @param date The date to compare with task due dates.
     * @return A list of tasks with due dates before the specified date.
     */
    List<Task> findByTaskDueDateBefore(LocalDate date);

    /**
     * Finds tasks with a due date after a specific date.
     *
     * @param date The date to compare with task due dates.
     * @return A list of tasks with due dates after the specified date.
     */
    List<Task> findByTaskDueDateAfter(LocalDate date);

    /**
     * Finds tasks with a due date between two specified dates.
     *
     * @param startDate The start date of the range to compare with task due dates.
     * @param endDate   The end date of the range to compare with task due dates.
     * @return A list of tasks with due dates within the specified range.
     */
    List<Task> findByTaskDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Finds tasks that have not yet been completed (completedAt is null).
     *
     * @return A list of incomplete tasks.
     */
    List<Task> findByCompletedAtIsNull();

    /**
     * Finds tasks that have been completed (completedAt is not null).
     *
     * @return A list of completed tasks.
     */
    List<Task> findByCompletedAtIsNotNull();

    /**
     * Finds tasks assigned to a specific user and having a particular status.
     *
     * @param assignee The assignee of the tasks to search for.
     * @param status   The status of the tasks to search for.
     * @return A list of tasks matching the given assignee and status criteria.
     */
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.status = :status")
    List<Task> findTasksByAssigneeAndStatus(@Param("assignee") User assignee, @Param("status") Status status);

    /**
     * Finds overdue tasks for a specific project.
     *
     * @param project The project for which to find overdue tasks.
     * @param date    The reference date to determine if a task is overdue.
     * @return A list of overdue tasks for the given project.
     */
    @Query("SELECT t FROM Task t WHERE t.project = :project AND t.taskDueDate < :date")
    List<Task> findOverdueTasksByProject(@Param("project") Project project, @Param("date") LocalDate date);


    /**
     * Finds tasks that have been soft-deleted (deletedAt is not null).
     *
     * @return A list of soft-deleted tasks.
     */
    @Query("SELECT t FROM Task t WHERE t.deletedAt IS NOT NULL")
    List<Task> findAllDeleted();
}
