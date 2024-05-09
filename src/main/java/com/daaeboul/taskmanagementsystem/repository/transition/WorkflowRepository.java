package com.daaeboul.taskmanagementsystem.repository.transition;

import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends BaseSoftDeletableRepository<Workflow, Long> {

    /**
     * Retrieves a list of Workflows whose names match the specified name.
     *
     * @param name The name to search for within Workflows.
     * @return A List of Workflow entities with a matching name.
     */
    List<Workflow> findByName(String name);

    /**
     * Finds workflows by name, including deleted ones, using pagination and sorting.
     *
     * @param name     The name (or part of the name) of the workflow to search for.
     * @param pageable Pagination and sorting parameters.
     * @return A Page of Workflow entities matching the criteria, including deleted workflows.
     */
    @Query("SELECT w FROM Workflow w WHERE w.name LIKE %:name%")
    Page<Workflow> findAllByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Finds all workflows, including deleted ones.
     *
     * @return A list of all Workflow entities, including deleted workflows.
     */
    @Query("SELECT w FROM Workflow w")
    List<Workflow> findAllIncludingDeleted();

    /**
     * Finds workflows that have at least one step associated with them.
     * This is useful for ensuring that workflows have defined steps for task progression.
     *
     * @return A list of Workflow entities that have at least one workflow step.
     */
    @Query("SELECT w FROM Workflow w JOIN w.workflowSteps ws WHERE ws IS NOT NULL")
    List<Workflow> findWithAtLeastOneStep();

    /**
     * Checks if a workflow with the specified name exists, regardless of deletion status.
     *
     * @param name The name of the workflow to check.
     * @return true if a workflow with the given name exists, false otherwise.
     */
    boolean existsByName(String name);
}
