package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.DuplicateTaskTypeNameException;
import com.daaeboul.taskmanagementsystem.exceptions.task.taskType.TaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.service.task.TaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-types")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    @Autowired
    public TaskTypeController(TaskTypeService taskTypeService) {
        this.taskTypeService = taskTypeService;
    }

    @PostMapping
    public ResponseEntity<TaskType> createTaskType(@RequestBody TaskType taskType) {
        try {
            TaskType createdTaskType = taskTypeService.createTaskType(taskType);
            return ResponseEntity.ok(createdTaskType);
        } catch (DuplicateTaskTypeNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskType> findTaskTypeById(@PathVariable Long id) {
        Optional<TaskType> taskType = taskTypeService.findTaskTypeById(id);
        return taskType.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TaskType>> findAllTaskTypes() {
        List<TaskType> taskTypes = taskTypeService.findAllTaskTypes();
        return ResponseEntity.ok(taskTypes);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TaskType>> findAllTaskTypes(Pageable pageable) {
        Page<TaskType> taskTypes = taskTypeService.findAllTaskTypes(pageable);
        return ResponseEntity.ok(taskTypes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskType>> findTaskTypesByNameContaining(@RequestParam String name) {
        List<TaskType> taskTypes = taskTypeService.findTaskTypesByNameContaining(name);
        return ResponseEntity.ok(taskTypes);
    }

    @GetMapping("/search/description")
    public ResponseEntity<List<TaskType>> findTaskTypesByDescriptionContaining(@RequestParam String description) {
        List<TaskType> taskTypes = taskTypeService.findTaskTypesByDescriptionContaining(description);
        return ResponseEntity.ok(taskTypes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskType> updateTaskType(@PathVariable Long id, @RequestBody TaskType taskTypeDetails) {
        Optional<TaskType> optionalTaskType = taskTypeService.findTaskTypeById(id);
        if (optionalTaskType.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TaskType existingTaskType = optionalTaskType.get();
        existingTaskType.setTaskTypeName(taskTypeDetails.getTaskTypeName());
        existingTaskType.setDescription(taskTypeDetails.getDescription());

        try {
            TaskType updatedTaskType = taskTypeService.updateTaskType(existingTaskType);
            return ResponseEntity.ok(updatedTaskType);
        } catch (DuplicateTaskTypeNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskType(@PathVariable Long id) {
        try {
            taskTypeService.deleteTaskType(id);
            return ResponseEntity.noContent().build();
        } catch (TaskTypeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
