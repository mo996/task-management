package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.DuplicateTaskPriorityNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority.TaskPriorityNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.service.task.TaskPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-priorities")
public class TaskPriorityController {

    private final TaskPriorityService taskPriorityService;

    @Autowired
    public TaskPriorityController(TaskPriorityService taskPriorityService) {
        this.taskPriorityService = taskPriorityService;
    }

    @PostMapping
    public ResponseEntity<TaskPriority> createTaskPriority(@RequestBody TaskPriority taskPriority) {
        try {
            TaskPriority createdTaskPriority = taskPriorityService.createTaskPriority(taskPriority);
            return ResponseEntity.ok(createdTaskPriority);
        } catch (DuplicateTaskPriorityNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskPriority> findTaskPriorityById(@PathVariable Long id) {
        Optional<TaskPriority> taskPriority = taskPriorityService.findTaskPriorityById(id);
        return taskPriority.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TaskPriority>> findAllTaskPriorities() {
        List<TaskPriority> taskPriorities = taskPriorityService.findAllTaskPriorities();
        return ResponseEntity.ok(taskPriorities);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TaskPriority>> findAllTaskPriorities(Pageable pageable) {
        Page<TaskPriority> taskPriorities = taskPriorityService.findAllTaskPriorities(pageable);
        return ResponseEntity.ok(taskPriorities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskPriority>> findTaskPrioritiesByNameContaining(@RequestParam String name) {
        List<TaskPriority> taskPriorities = taskPriorityService.findTaskPrioritiesByNameContaining(name);
        return ResponseEntity.ok(taskPriorities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskPriority> updateTaskPriority(@PathVariable Long id, @RequestBody TaskPriority taskPriorityDetails) {
        Optional<TaskPriority> optionalTaskPriority = taskPriorityService.findTaskPriorityById(id);
        if (optionalTaskPriority.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TaskPriority existingTaskPriority = optionalTaskPriority.get();
        existingTaskPriority.setPriorityName(taskPriorityDetails.getPriorityName());

        try {
            TaskPriority updatedTaskPriority = taskPriorityService.updateTaskPriority(existingTaskPriority);
            return ResponseEntity.ok(updatedTaskPriority);
        } catch (DuplicateTaskPriorityNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskPriority(@PathVariable Long id) {
        try {
            taskPriorityService.deleteTaskPriority(id);
            return ResponseEntity.noContent().build();
        } catch (TaskPriorityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
