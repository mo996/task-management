package com.daaeboul.taskmanagementsystem.controller.task;


import com.daaeboul.taskmanagementsystem.exceptions.task.task.TaskNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
import com.daaeboul.taskmanagementsystem.service.project.ProjectService;
import com.daaeboul.taskmanagementsystem.service.transition.StatusService;
import com.daaeboul.taskmanagementsystem.service.task.CategoryService;
import com.daaeboul.taskmanagementsystem.service.task.TaskPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final ProjectService projectService;
    private final StatusService statusService;
    private final CategoryService categoryService;
    private final TaskPriorityService taskPriorityService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, ProjectService projectService, StatusService statusService, CategoryService categoryService, TaskPriorityService taskPriorityService) {
        this.taskService = taskService;
        this.userService = userService;
        this.projectService = projectService;
        this.statusService = statusService;
        this.categoryService = categoryService;
        this.taskPriorityService = taskPriorityService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.findTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Task>> findAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        try {
            Optional<Task> optionalTask = taskService.findTaskById(id);
            if (!optionalTask.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Task existingTask = optionalTask.get();

            if (taskDetails.getAssignee() != null) {
                User assignee = userService.findUserById(taskDetails.getAssignee().getId())
                        .orElseThrow(() -> new TaskNotFoundException("Assignee not found"));
                existingTask.setAssignee(assignee);
            }

            if (taskDetails.getCategory() != null) {
                Category category = categoryService.findCategoryById(taskDetails.getCategory().getId())
                        .orElseThrow(() -> new TaskNotFoundException("Category not found"));
                existingTask.setCategory(category);
            }

            if (taskDetails.getPriority() != null) {
                TaskPriority priority = taskPriorityService.findTaskPriorityById(taskDetails.getPriority().getId())
                        .orElseThrow(() -> new TaskNotFoundException("Priority not found"));
                existingTask.setPriority(priority);
            }

            if (taskDetails.getProject() != null) {
                Project project = projectService.findProjectById(taskDetails.getProject().getId())
                        .orElseThrow(() -> new TaskNotFoundException("Project not found"));
                existingTask.setProject(project);
            }

            if (taskDetails.getStatus() != null) {
                Status status = statusService.findStatusById(taskDetails.getStatus().getId())
                        .orElseThrow(() -> new TaskNotFoundException("Status not found"));
                existingTask.setStatus(status);
            }

            existingTask.setTaskTitle(taskDetails.getTaskTitle());
            existingTask.setTaskDescription(taskDetails.getTaskDescription());
            existingTask.setTaskDueDate(taskDetails.getTaskDueDate());
            existingTask.setTaskType(taskDetails.getTaskType());
            existingTask.setCompletedAt(taskDetails.getCompletedAt());
            existingTask.setDependencies(taskDetails.getDependencies());
            existingTask.setPrecedencies(taskDetails.getPrecedencies());

            Task updatedTask = taskService.updateTask(existingTask);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<Task>> findTasksByAssignee(@PathVariable Long assigneeId) {
        User assignee = userService.findUserById(assigneeId)
                .orElseThrow(() -> new TaskNotFoundException("Assignee not found"));
        List<Task> tasks = taskService.findTasksByAssignee(assignee);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Task>> findTasksByCategory(@PathVariable Long categoryId) {
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new TaskNotFoundException("Category not found"));
        List<Task> tasks = taskService.findTasksByCategory(category);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priorityId}")
    public ResponseEntity<List<Task>> findTasksByPriority(@PathVariable Long priorityId) {
        TaskPriority priority = taskPriorityService.findTaskPriorityById(priorityId)
                .orElseThrow(() -> new TaskNotFoundException("Priority not found"));
        List<Task> tasks = taskService.findTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> findTasksByProject(@PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId)
                .orElseThrow(() -> new TaskNotFoundException("Project not found"));
        List<Task> tasks = taskService.findTasksByProject(project);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Task>> findTasksByStatus(@PathVariable Long statusId) {
        Status status = statusService.findStatusById(statusId)
                .orElseThrow(() -> new TaskNotFoundException("Status not found"));
        List<Task> tasks = taskService.findTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-before/{date}")
    public ResponseEntity<List<Task>> findTasksDueBefore(@PathVariable LocalDate date) {
        List<Task> tasks = taskService.findTasksDueBefore(date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-after/{date}")
    public ResponseEntity<List<Task>> findTasksDueAfter(@PathVariable LocalDate date) {
        List<Task> tasks = taskService.findTasksDueAfter(date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-between")
    public ResponseEntity<List<Task>> findTasksDueBetween(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<Task> tasks = taskService.findTasksDueBetween(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/incomplete")
    public ResponseEntity<List<Task>> findIncompleteTasks() {
        List<Task> tasks = taskService.findIncompleteTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Task>> findCompletedTasks() {
        List<Task> tasks = taskService.findCompletedTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assigneeId}/status/{statusId}")
    public ResponseEntity<List<Task>> findTasksByAssigneeAndStatus(@PathVariable Long assigneeId, @PathVariable Long statusId) {
        User assignee = userService.findUserById(assigneeId)
                .orElseThrow(() -> new TaskNotFoundException("Assignee not found"));
        Status status = statusService.findStatusById(statusId)
                .orElseThrow(() -> new TaskNotFoundException("Status not found"));
        List<Task> tasks = taskService.findTasksByAssigneeAndStatus(assignee, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue/project/{projectId}/date/{date}")
    public ResponseEntity<List<Task>> findOverdueTasksByProject(@PathVariable Long projectId, @PathVariable LocalDate date) {
        Project project = projectService.findProjectById(projectId)
                .orElseThrow(() -> new TaskNotFoundException("Project not found"));
        List<Task> tasks = taskService.findOverdueTasksByProject(project, date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<Task>> findAllDeletedTasks() {
        List<Task> tasks = taskService.findAllDeletedTasks();
        return ResponseEntity.ok(tasks);
    }
}