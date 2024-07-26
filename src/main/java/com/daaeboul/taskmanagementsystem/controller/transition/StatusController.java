package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.status.DuplicateStatusNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.status.StatusNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.service.transition.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/statuses")
public class StatusController {

    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping
    public ResponseEntity<Status> createStatus(@RequestBody Status status) {
        try {
            Status createdStatus = statusService.createStatus(status);
            return ResponseEntity.ok(createdStatus);
        } catch (DuplicateStatusNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Status> findStatusById(@PathVariable Long id) {
        Optional<Status> status = statusService.findStatusById(id);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Status>> findAllStatuses() {
        List<Status> statuses = statusService.findAllStatuses();
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Status>> findAllStatuses(Pageable pageable) {
        Page<Status> statuses = statusService.findAllStatuses(pageable);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Status>> findStatusesByNameContaining(@RequestParam String name, Pageable pageable) {
        Page<Status> statuses = statusService.findStatusesByNameContaining(name, pageable);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/search/description")
    public ResponseEntity<List<Status>> findStatusesByDescriptionContaining(@RequestParam String description) {
        List<Status> statuses = statusService.findStatusesByDescriptionContaining(description);
        return ResponseEntity.ok(statuses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @RequestBody Status statusDetails) {
        Optional<Status> optionalStatus = statusService.findStatusById(id);
        if (optionalStatus.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Status existingStatus = optionalStatus.get();
        existingStatus.setStatusName(statusDetails.getStatusName());
        existingStatus.setDescription(statusDetails.getDescription());

        try {
            Status updatedStatus = statusService.updateStatus(existingStatus);
            return ResponseEntity.ok(updatedStatus);
        } catch (DuplicateStatusNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        try {
            statusService.deleteStatus(id);
            return ResponseEntity.noContent().build();
        } catch (StatusNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/created-before")
    public ResponseEntity<List<Status>> findStatusesCreatedBefore(@RequestParam LocalDateTime dateTime) {
        List<Status> statuses = statusService.findStatusesCreatedBefore(dateTime);
        return ResponseEntity.ok(statuses);
    }
}
