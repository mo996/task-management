package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskComment.TaskCommentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.task.TaskCommentService;
import com.daaeboul.taskmanagementsystem.service.task.TaskService;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-comments")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskCommentController(TaskCommentService taskCommentService, TaskService taskService, UserService userService) {
        this.taskCommentService = taskCommentService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TaskComment> createTaskComment(@RequestParam Long taskId, @RequestParam Long userId, @RequestBody TaskComment comment) {
        Task task = taskService.findTaskById(taskId)
                .orElseThrow(() -> new TaskCommentNotFoundException("Task not found"));
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new TaskCommentNotFoundException("User not found"));
        TaskComment createdComment = taskCommentService.createTaskComment(task, user, comment);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskComment> findTaskCommentById(@PathVariable Long id) {
        Optional<TaskComment> comment = taskCommentService.findTaskCommentById(id);
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TaskComment>> findAllTaskComments() {
        List<TaskComment> comments = taskCommentService.findAllTaskComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskComment>> findTaskCommentsByTask(@PathVariable Long taskId) {
        Task task = taskService.findTaskById(taskId)
                .orElseThrow(() -> new TaskCommentNotFoundException("Task not found"));
        List<TaskComment> comments = taskCommentService.findTaskCommentsByTask(task);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/task/{taskId}/paged")
    public ResponseEntity<Page<TaskComment>> findTaskCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        Task task = taskService.findTaskById(taskId)
                .orElseThrow(() -> new TaskCommentNotFoundException("Task not found"));
        Page<TaskComment> comments = taskCommentService.findTaskCommentsByTask(task, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskComment>> findTaskCommentsByUser(@PathVariable Long userId) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new TaskCommentNotFoundException("User not found"));
        List<TaskComment> comments = taskCommentService.findTaskCommentsByUser(user);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<TaskComment>> findTaskCommentsByUser(@PathVariable Long userId, Pageable pageable) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new TaskCommentNotFoundException("User not found"));
        Page<TaskComment> comments = taskCommentService.findTaskCommentsByUser(user, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskComment>> findTaskCommentsByKeyword(@RequestParam String keyword) {
        List<TaskComment> comments = taskCommentService.findTaskCommentsByKeyword(keyword);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search/paged")
    public ResponseEntity<Page<TaskComment>> findTaskCommentsByKeyword(@RequestParam String keyword, Pageable pageable) {
        Page<TaskComment> comments = taskCommentService.findTaskCommentsByKeyword(keyword, pageable);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskComment> updateTaskComment(@PathVariable Long id, @RequestBody TaskComment commentDetails) {
        TaskComment existingComment = taskCommentService.findTaskCommentById(id)
                .orElseThrow(() -> new TaskCommentNotFoundException("Task comment not found"));
        existingComment.setComment(commentDetails.getComment());
        TaskComment updatedComment = taskCommentService.updateTaskComment(existingComment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskComment(@PathVariable Long id) {
        try {
            taskCommentService.deleteTaskComment(id);
            return ResponseEntity.noContent().build();
        } catch (TaskCommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<TaskComment>> findAllTaskCommentsIncludingDeleted() {
        List<TaskComment> comments = taskCommentService.findAllTaskCommentsIncludingDeleted();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/task/{taskId}/deleted")
    public ResponseEntity<List<TaskComment>> findAllTaskCommentsByTaskIdIncludingDeleted(@PathVariable Long taskId) {
        List<TaskComment> comments = taskCommentService.findAllTaskCommentsByTaskIdIncludingDeleted(taskId);
        return ResponseEntity.ok(comments);
    }
}
