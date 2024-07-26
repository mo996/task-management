package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskAttachment.TaskAttachmentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import com.daaeboul.taskmanagementsystem.service.task.TaskAttachmentService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-attachments")
public class TaskAttachmentController {

    private final TaskAttachmentService taskAttachmentService;
    private final TaskService taskService;

    @Autowired
    public TaskAttachmentController(TaskAttachmentService taskAttachmentService, TaskService taskService) {
        this.taskAttachmentService = taskAttachmentService;
        this.taskService = taskService;
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<TaskAttachment> createTaskAttachment(@PathVariable Long taskId, @RequestBody TaskAttachment taskAttachment) {
        Optional<Task> taskOptional = taskService.findTaskById(taskId);
        if (taskOptional.isPresent()) {
            TaskAttachment createdTaskAttachment = taskAttachmentService.createTaskAttachment(taskOptional.get(), taskAttachment);
            return ResponseEntity.ok(createdTaskAttachment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskAttachment> findTaskAttachmentById(@PathVariable Long id) {
        return taskAttachmentService.findTaskAttachmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskAttachment>> findTaskAttachmentsByTaskId(@PathVariable Long taskId) {
        List<TaskAttachment> taskAttachments = taskAttachmentService.findTaskAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(taskAttachments);
    }

    @GetMapping("/search/file-name/{fileName}")
    public ResponseEntity<List<TaskAttachment>> findTaskAttachmentsByFileName(@PathVariable String fileName) {
        List<TaskAttachment> taskAttachments = taskAttachmentService.findTaskAttachmentsByFileName(fileName);
        return ResponseEntity.ok(taskAttachments);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskAttachment> updateTaskAttachment(@PathVariable Long id, @RequestBody TaskAttachment taskAttachmentDetails) {
        try {
            // Fetch the existing task attachment by ID
            TaskAttachment existingTaskAttachment = taskAttachmentService.findTaskAttachmentById(id)
                    .orElseThrow(() -> new TaskAttachmentNotFoundException("Task attachment not found with ID: " + id));

            // Update the existing task attachment with the provided details
            existingTaskAttachment.setFileName(taskAttachmentDetails.getFileName());
            existingTaskAttachment.setFileSize(taskAttachmentDetails.getFileSize());
            existingTaskAttachment.setFileType(taskAttachmentDetails.getFileType());
            existingTaskAttachment.setFileContent(taskAttachmentDetails.getFileContent());

            // Save the updated task attachment
            TaskAttachment updatedTaskAttachment = taskAttachmentService.updateTaskAttachment(existingTaskAttachment);
            return ResponseEntity.ok(updatedTaskAttachment);
        } catch (TaskAttachmentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskAttachment(@PathVariable Long id) {
        try {
            taskAttachmentService.deleteTaskAttachment(id);
            return ResponseEntity.noContent().build();
        } catch (TaskAttachmentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
