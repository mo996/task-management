package com.daaeboul.taskmanagementsystem.service.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflowStep.WorkflowStepNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep.WorkflowStepId;
import com.daaeboul.taskmanagementsystem.repository.transition.WorkflowStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowStepService {

    private final WorkflowStepRepository workflowStepRepository;

    @Autowired
    public WorkflowStepService(WorkflowStepRepository workflowStepRepository) {
        this.workflowStepRepository = workflowStepRepository;
    }

    /**
     * Creates a new workflow step.
     *
     * @param workflowStep The workflow step to create.
     * @return The created workflow step.
     */
    @Transactional
    public WorkflowStep createWorkflowStep(WorkflowStep workflowStep) {
        return workflowStepRepository.save(workflowStep);
    }

    /**
     * Finds a workflow step by its composite ID.
     *
     * @param id The composite ID of the workflow step.
     * @return An Optional containing the workflow step if found, otherwise empty.
     */
    public Optional<WorkflowStep> findWorkflowStepById(WorkflowStepId id) {
        return workflowStepRepository.findById(id);
    }

    /**
     * Finds workflow steps by workflow ID and status name.
     *
     * @param workflowId The ID of the workflow.
     * @param statusName The name of the status.
     * @return A list of WorkflowStep entities matching the criteria.
     */
    public List<WorkflowStep> findWorkflowStepsByWorkflowIdAndStatusName(Long workflowId, String statusName) {
        return workflowStepRepository.findByWorkflowIdAndStatusName(workflowId, statusName);
    }

    /**
     * Finds workflow steps by workflow name and status name.
     *
     * @param workflowName The name of the workflow.
     * @param statusName   The name of the status.
     * @return A list of WorkflowStep entities matching the criteria.
     */
    public List<WorkflowStep> findWorkflowStepsByWorkflowNameAndStatusName(String workflowName, String statusName) {
        return workflowStepRepository.findByWorkflowNameAndStatusName(workflowName, statusName);
    }

    /**
     * Finds all workflow steps associated with a specific workflow, including those with deleted workflows.
     *
     * @param workflowId The ID of the workflow.
     * @return A list of WorkflowStep entities associated with the specified workflow.
     */
    public List<WorkflowStep> findAllWorkflowStepsByWorkflowId(Long workflowId) {
        return workflowStepRepository.findAllByWorkflowId(workflowId);
    }

    /**
     * Finds all workflow steps associated with a specific workflow, including those with deleted workflows, using pagination and sorting.
     *
     * @param workflowId The ID of the workflow.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of WorkflowStep entities associated with the specified workflow.
     */
    public Page<WorkflowStep> findAllWorkflowStepsByWorkflowId(Long workflowId, Pageable pageable) {
        return workflowStepRepository.findAllByWorkflowId(workflowId, pageable);
    }

    /**
     * Updates a workflow step.
     *
     * @param updatedWorkflowStep The workflow step with updated information.
     * @return The updated workflow step.
     * @throws WorkflowStepNotFoundException If the workflow step is not found.
     */
    @Transactional
    public WorkflowStep updateWorkflowStep(WorkflowStep updatedWorkflowStep) {
        WorkflowStep existingWorkflowStep = workflowStepRepository.findById(updatedWorkflowStep.getId())
                .orElseThrow(() -> new WorkflowStepNotFoundException("Workflow step not found"));

        existingWorkflowStep.setWorkflow(updatedWorkflowStep.getWorkflow());
        existingWorkflowStep.setStatus(updatedWorkflowStep.getStatus());

        return workflowStepRepository.save(existingWorkflowStep);
    }

    /**
     * Deletes a workflow step by its composite ID.
     *
     * @param id The composite ID of the workflow step to delete.
     * @throws WorkflowStepNotFoundException If the workflow step is not found.
     */
    @Transactional
    public void deleteWorkflowStep(WorkflowStepId id) {
        if (!workflowStepRepository.existsById(id)) {
            throw new WorkflowStepNotFoundException("Workflow step not found");
        }
        workflowStepRepository.deleteById(id);
    }
}