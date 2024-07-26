package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.DuplicateWorkflowNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.WorkflowNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.service.transition.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        try {
            Workflow createdWorkflow = workflowService.createWorkflow(workflow);
            return ResponseEntity.ok(createdWorkflow);
        } catch (DuplicateWorkflowNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> findWorkflowById(@PathVariable Long id) {
        Optional<Workflow> workflow = workflowService.findWorkflowById(id);
        return workflow.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> findAllWorkflows() {
        List<Workflow> workflows = workflowService.findAllWorkflows();
        return ResponseEntity.ok(workflows);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Workflow>> findAllWorkflows(Pageable pageable) {
        Page<Workflow> workflows = workflowService.findAllWorkflows(pageable);
        return ResponseEntity.ok(workflows);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Workflow>> findWorkflowsByNameContaining(@RequestParam String name, Pageable pageable) {
        Page<Workflow> workflows = workflowService.findWorkflowsByNameContaining(name, pageable);
        return ResponseEntity.ok(workflows);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @RequestBody Workflow workflowDetails) {
        Optional<Workflow> optionalWorkflow = workflowService.findWorkflowById(id);
        if (optionalWorkflow.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Workflow existingWorkflow = optionalWorkflow.get();
        existingWorkflow.setName(workflowDetails.getName());
        existingWorkflow.setDescription(workflowDetails.getDescription());
        existingWorkflow.setWorkflowSteps(workflowDetails.getWorkflowSteps());

        try {
            Workflow updatedWorkflow = workflowService.updateWorkflow(existingWorkflow);
            return ResponseEntity.ok(updatedWorkflow);
        } catch (DuplicateWorkflowNameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        try {
            workflowService.deleteWorkflow(id);
            return ResponseEntity.noContent().build();
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/with-steps")
    public ResponseEntity<List<Workflow>> findWorkflowsWithAtLeastOneStep() {
        List<Workflow> workflows = workflowService.findWorkflowsWithAtLeastOneStep();
        return ResponseEntity.ok(workflows);
    }

}
