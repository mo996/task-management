package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskDependency;
import com.daaeboul.taskmanagementsystem.service.task.TaskDependencyService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-dependencies")
public class TaskDependencyController {

    private final TaskDependencyService taskDependencyService;
    private final TaskService taskService;

    @Autowired
    public TaskDependencyController(TaskDependencyService taskDependencyService, TaskService taskService) {
        this.taskDependencyService = taskDependencyService;
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDependency> createTaskDependency(@RequestBody TaskDependency taskDependency) {
        TaskDependency createdTaskDependency = taskDependencyService.createTaskDependency(taskDependency);
        return ResponseEntity.ok(createdTaskDependency);
    }

    @GetMapping("/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<TaskDependency> findTaskDependencyById(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        TaskDependency.TaskDependencyId id = new TaskDependency.TaskDependencyId(taskId, dependsOnTaskId);
        Optional<TaskDependency> taskDependency = taskDependencyService.findTaskDependencyById(id);
        return taskDependency.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskDependency>> findTaskDependenciesByTaskId(@PathVariable Long taskId) {
        List<TaskDependency> taskDependencies = taskDependencyService.findTaskDependenciesByTaskId(taskId);
        return ResponseEntity.ok(taskDependencies);
    }

    @GetMapping("/task/{taskId}/paged")
    public ResponseEntity<Page<TaskDependency>> findTaskDependenciesByTaskId(@PathVariable Long taskId, Pageable pageable) {
        Page<TaskDependency> taskDependencies = taskDependencyService.findTaskDependenciesByTaskId(taskId, pageable);
        return ResponseEntity.ok(taskDependencies);
    }

    @GetMapping("/depends-on-task/{dependsOnTaskId}")
    public ResponseEntity<List<TaskDependency>> findTaskDependenciesByDependsOnTaskId(@PathVariable Long dependsOnTaskId) {
        List<TaskDependency> taskDependencies = taskDependencyService.findTaskDependenciesByDependsOnTaskId(dependsOnTaskId);
        return ResponseEntity.ok(taskDependencies);
    }

    @PutMapping("/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<TaskDependency> updateTaskDependency(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId, @RequestBody TaskDependency taskDependencyDetails) {
        TaskDependency.TaskDependencyId id = new TaskDependency.TaskDependencyId(taskId, dependsOnTaskId);
        taskDependencyDetails.setId(id);

        TaskDependency updatedTaskDependency = taskDependencyService.updateTaskDependency(taskDependencyDetails);
        return ResponseEntity.ok(updatedTaskDependency);
    }

    @DeleteMapping("/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<Void> deleteTaskDependency(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        TaskDependency.TaskDependencyId id = new TaskDependency.TaskDependencyId(taskId, dependsOnTaskId);
        taskDependencyService.deleteTaskDependency(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<Boolean> existsTaskDependency(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        boolean exists = taskDependencyService.existsTaskDependency(taskId, dependsOnTaskId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count-dependencies/{taskId}")
    public ResponseEntity<Long> countDependenciesForTask(@PathVariable Long taskId) {
        long count = taskDependencyService.countDependenciesForTask(taskId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-dependents/{dependsOnTaskId}")
    public ResponseEntity<Long> countDependentsForTask(@PathVariable Long dependsOnTaskId) {
        long count = taskDependencyService.countDependentsForTask(dependsOnTaskId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/direct-dependencies/{taskId}")
    public ResponseEntity<List<Task>> findDirectDependenciesForTask(@PathVariable Long taskId) {
        List<Task> dependencies = taskDependencyService.findDirectDependenciesForTask(taskId);
        return ResponseEntity.ok(dependencies);
    }

    @GetMapping("/direct-dependents/{taskId}")
    public ResponseEntity<List<Task>> findDirectDependentsForTask(@PathVariable Long taskId) {
        List<Task> dependents = taskDependencyService.findDirectDependentsForTask(taskId);
        return ResponseEntity.ok(dependents);
    }
}
