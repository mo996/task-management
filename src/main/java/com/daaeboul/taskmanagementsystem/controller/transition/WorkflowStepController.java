package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflowStep.WorkflowStepNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep.WorkflowStepId;
import com.daaeboul.taskmanagementsystem.service.transition.WorkflowStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/workflow-steps")
public class WorkflowStepController {

    private final WorkflowStepService workflowStepService;

    @Autowired
    public WorkflowStepController(WorkflowStepService workflowStepService) {
        this.workflowStepService = workflowStepService;
    }

    @PostMapping
    public ResponseEntity<WorkflowStep> createWorkflowStep(@RequestBody WorkflowStep workflowStep) {
        WorkflowStep createdWorkflowStep = workflowStepService.createWorkflowStep(workflowStep);
        return ResponseEntity.ok(createdWorkflowStep);
    }

    @GetMapping("/{workflowId}/{statusId}")
    public ResponseEntity<WorkflowStep> findWorkflowStepById(@PathVariable Long workflowId, @PathVariable Long statusId) {
        WorkflowStepId id = new WorkflowStepId();
        id.setStatusId(statusId);
        id.setWorkflowId(workflowId);
        Optional<WorkflowStep> workflowStep = workflowStepService.findWorkflowStepById(id);
        return workflowStep.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/workflow/{workflowId}")
    public ResponseEntity<List<WorkflowStep>> findAllWorkflowStepsByWorkflowId(@PathVariable Long workflowId) {
        List<WorkflowStep> workflowSteps = workflowStepService.findAllWorkflowStepsByWorkflowId(workflowId);
        return ResponseEntity.ok(workflowSteps);
    }

    @GetMapping("/workflow/{workflowId}/page")
    public ResponseEntity<Page<WorkflowStep>> findAllWorkflowStepsByWorkflowId(@PathVariable Long workflowId, Pageable pageable) {
        Page<WorkflowStep> workflowSteps = workflowStepService.findAllWorkflowStepsByWorkflowId(workflowId, pageable);
        return ResponseEntity.ok(workflowSteps);
    }

    @GetMapping("/workflow/{workflowId}/status/{statusName}")
    public ResponseEntity<List<WorkflowStep>> findWorkflowStepsByWorkflowIdAndStatusName(@PathVariable Long workflowId, @PathVariable String statusName) {
        List<WorkflowStep> workflowSteps = workflowStepService.findWorkflowStepsByWorkflowIdAndStatusName(workflowId, statusName);
        return ResponseEntity.ok(workflowSteps);
    }

    @GetMapping("/workflow-name/{workflowName}/status/{statusName}")
    public ResponseEntity<List<WorkflowStep>> findWorkflowStepsByWorkflowNameAndStatusName(@PathVariable String workflowName, @PathVariable String statusName) {
        List<WorkflowStep> workflowSteps = workflowStepService.findWorkflowStepsByWorkflowNameAndStatusName(workflowName, statusName);
        return ResponseEntity.ok(workflowSteps);
    }

    @PutMapping("/{workflowId}/{statusId}")
    public ResponseEntity<WorkflowStep> updateWorkflowStep(@PathVariable Long workflowId, @PathVariable Long statusId, @RequestBody WorkflowStep workflowStepDetails) {
        WorkflowStepId id = new WorkflowStepId();
        id.setStatusId(statusId);
        id.setWorkflowId(workflowId);
        Optional<WorkflowStep> optionalWorkflowStep = workflowStepService.findWorkflowStepById(id);
        if (optionalWorkflowStep.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WorkflowStep existingWorkflowStep = optionalWorkflowStep.get();
        existingWorkflowStep.setWorkflow(workflowStepDetails.getWorkflow());
        existingWorkflowStep.setStatus(workflowStepDetails.getStatus());
        existingWorkflowStep.setSequenceNumber(workflowStepDetails.getSequenceNumber());

        WorkflowStep updatedWorkflowStep = workflowStepService.updateWorkflowStep(existingWorkflowStep);
        return ResponseEntity.ok(updatedWorkflowStep);
    }

    @DeleteMapping("/{workflowId}/{statusId}")
    public ResponseEntity<Void> deleteWorkflowStep(@PathVariable Long workflowId, @PathVariable Long statusId) {
        WorkflowStepId id = new WorkflowStepId();
        id.setStatusId(statusId);
        id.setWorkflowId(workflowId);
        try {
            workflowStepService.deleteWorkflowStep(id);
            return ResponseEntity.noContent().build();
        } catch (WorkflowStepNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
