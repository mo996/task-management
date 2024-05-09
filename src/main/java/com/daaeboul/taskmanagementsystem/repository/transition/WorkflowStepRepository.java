package com.daaeboul.taskmanagementsystem.repository.transition;

import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep.WorkflowStepId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, WorkflowStepId> {

    /**
     * Finds workflow steps by workflow ID and status name.
     *
     * @param workflowId The ID of the workflow.
     * @param statusName The name of the status.
     * @return A list of WorkflowStep entities matching the criteria.
     */
    @Query("SELECT ws FROM WorkflowStep ws JOIN ws.status s WHERE ws.workflow.id = :workflowId AND s.statusName = :statusName")
    List<WorkflowStep> findByWorkflowIdAndStatusName(@Param("workflowId") Long workflowId, @Param("statusName") String statusName);

    /**
     * Finds workflow steps by workflow name and status name.
     *
     * @param workflowName The name of the workflow.
     * @param statusName   The name of the status.
     * @return A list of WorkflowStep entities matching the criteria.
     */
    @Query("SELECT ws FROM WorkflowStep ws JOIN ws.workflow w JOIN ws.status s WHERE w.name = :workflowName AND s.statusName = :statusName")
    List<WorkflowStep> findByWorkflowNameAndStatusName(@Param("workflowName") String workflowName, @Param("statusName") String statusName);

    /**
     * Finds all workflow steps associated with a specific workflow, including those with deleted workflows.
     *
     * @param workflowId The ID of the workflow.
     * @return A list of WorkflowStep entities associated with the specified workflow.
     */
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflow.id = :workflowId")
    List<WorkflowStep> findAllByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * Finds all workflow steps associated with a specific workflow, including those with deleted workflows, using pagination and sorting.
     *
     * @param workflowId The ID of the workflow.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of WorkflowStep entities associated with the specified workflow.
     */
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflow.id = :workflowId")
    Page<WorkflowStep> findAllByWorkflowId(@Param("workflowId") Long workflowId, Pageable pageable);

    /**
     * Counts the number of workflow steps for a given workflow ID, including steps with deleted workflows.
     *
     * @param workflowId The ID of the workflow.
     * @return The count of WorkflowStep entities associated with the specified workflow.
     */
    long countByWorkflowId(@Param("workflowId") Long workflowId);
}
