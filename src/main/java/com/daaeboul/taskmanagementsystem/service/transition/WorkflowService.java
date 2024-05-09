package com.daaeboul.taskmanagementsystem.service.transition;


import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.DuplicateWorkflowNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.WorkflowNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.repository.transition.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Autowired
    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    /**
     * Finds workflows by their exact name.
     *
     * @param name the name of The workflow to search for.
     * @return A list of Workflows entities matching the exact name, including deleted ones
     */
    public List<Workflow> findWorkflowsByName(String name){
        return workflowRepository.findByName(name);
    }

    /**
     * Creates a new workflow.
     *
     * @param workflow The workflow to create.
     * @return The created workflow.
     * @throws DuplicateWorkflowNameException If a workflow with the same name already exists.
     */
    @Transactional
    public Workflow createWorkflow(Workflow workflow) {
        if (workflowRepository.existsByName(workflow.getName())) {
            throw new DuplicateWorkflowNameException("Workflow name already exists: " + workflow.getName());
        }
        return workflowRepository.save(workflow);
    }

    /**
     * Finds a workflow by its ID.
     *
     * @param id The ID of the workflow.
     * @return An Optional containing the workflow if found, otherwise empty.
     */
    public Optional<Workflow> findWorkflowById(Long id) {
        return workflowRepository.findById(id);
    }

    /**
     * Finds all workflows.
     *
     * @return A list of all workflows, including deleted ones.
     */
    public List<Workflow> findAllWorkflows() {
        return workflowRepository.findAllIncludingDeleted();
    }

    /**
     * Finds all workflows with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of workflows, including deleted ones.
     */
    public Page<Workflow> findAllWorkflows(Pageable pageable) {
        return workflowRepository.findAll(pageable);
    }

    /**
     * Finds workflows by name, including deleted ones, using pagination and sorting.
     *
     * @param name     The name (or part of the name) of the workflow to search for.
     * @param pageable Pagination and sorting parameters.
     * @return A Page of Workflow entities matching the criteria, including deleted workflows.
     */
    public Page<Workflow> findWorkflowsByNameContaining(String name, Pageable pageable) {
        return workflowRepository.findAllByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Updates a workflow.
     *
     * @param updatedWorkflow The workflow with updated information.
     * @return The updated workflow.
     * @throws WorkflowNotFoundException If the workflow is not found.
     */
    @Transactional
    public Workflow updateWorkflow(Workflow updatedWorkflow) {
        Workflow existingWorkflow = workflowRepository.findById(updatedWorkflow.getId())
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found with ID: " + updatedWorkflow.getId()));

        existingWorkflow.setName(updatedWorkflow.getName());
        existingWorkflow.setDescription(updatedWorkflow.getDescription());
        existingWorkflow.setWorkflowSteps(updatedWorkflow.getWorkflowSteps());

        return workflowRepository.save(existingWorkflow);
    }

    /**
     * Deletes a workflow by its ID.
     *
     * @param id The ID of the workflow to delete.
     * @throws WorkflowNotFoundException If the workflow is not found.
     */
    @Transactional
    public void deleteWorkflow(Long id) {
        if (!workflowRepository.existsById(id)) {
            throw new WorkflowNotFoundException("Workflow not found with ID: " + id);
        }
        workflowRepository.deleteById(id);
    }

    /**
     * Finds workflows that have at least one step associated with them.
     * This is useful for ensuring that workflows have defined steps for task progression.
     *
     * @return A list of Workflow entities that have at least one workflow step.
     */
    public List<Workflow> findWorkflowsWithAtLeastOneStep() {
        return workflowRepository.findWithAtLeastOneStep();
    }
}
