package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType.ProjectTaskTypeId;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskTypeRepository extends JpaRepository<ProjectTaskType, ProjectTaskTypeId> {

    /**
     * Retrieves a list of ProjectTaskTypes associated with a specific project.
     *
     * @param projectName The name of the project to filter ProjectTaskTypes by.
     * @return A List of ProjectTaskTypes belonging to the specified project.
     */
    @Query("SELECT ptt FROM ProjectTaskType ptt JOIN ptt.project p WHERE p.projectName = :projectName")
    List<ProjectTaskType> findByProjectName(@Param("projectName") String projectName);

    /**
     * Retrieves a list of ProjectTaskTypes associated with a specific TaskType.
     *
     * @param taskTypeName The name of the TaskType to filter ProjectTaskTypes by.
     * @return A List of ProjectTaskTypes linked to the specified TaskType.
     */
    @Query("SELECT ptt FROM ProjectTaskType ptt JOIN ptt.taskType tt WHERE tt.taskTypeName = :taskTypeName")
    List<ProjectTaskType> findByTaskTypeName(@Param("taskTypeName") String taskTypeName);

    /**
     * Retrieves a list of ProjectTaskTypes belonging to a specific Workflow.
     *
     * @param workflowName The name of the Workflow to filter ProjectTaskTypes by.
     * @return A List of ProjectTaskTypes belonging to the specified Workflow.
     */
    @Query("SELECT ptt FROM ProjectTaskType ptt WHERE ptt.workflow.name = :workflowName")
    List<ProjectTaskType> findByWorkflowName(@Param("workflowName") String workflowName);

    /**
     * Retrieves a distinct list of Projects associated with ProjectTaskTypes that belong to a Workflow with the specified name.
     * This query likely handles a situation where ProjectTaskType has a composite primary key involving the Workflow.
     *
     * @param workflowName The name of the Workflow to filter by.
     * @return A List of distinct Projects that utilize the specified Workflow within their ProjectTaskTypes.
     */
    @Query("SELECT DISTINCT p FROM ProjectTaskType ptt JOIN ptt.project p WHERE ptt.workflow.name = :workflowName")
    List<Project> findProjectsUsingWorkflowByName(@Param("workflowName") String workflowName);

    /**
     * Retrieves a distinct list of TaskTypes associated with ProjectTaskTypes belonging to the specified Project.
     * This query likely handles a scenario where ProjectTaskType has a composite primary key involving the Project.
     *
     * @param projectName The name of the Project to filter by.
     * @return A List of distinct TaskTypes used within the specified Project's ProjectTaskTypes.
     */
    @Query("SELECT DISTINCT tt FROM ProjectTaskType ptt JOIN ptt.taskType tt JOIN ptt.project p WHERE p.projectName = :projectName")
    List<TaskType> findTaskTypesByProjectName(@Param("projectName") String projectName);

    /**
     * Counts the number of distinct TaskTypes associated with a specific Project.
     * This query also likely addresses the presence of a composite primary key in the ProjectTaskType entity.
     *
     * @param projectName The name of the Project to filter by.
     * @return The count of distinct TaskTypes utilized within the specified Project.
     */
    @Query("SELECT COUNT(DISTINCT tt) FROM ProjectTaskType ptt JOIN ptt.taskType tt JOIN ptt.project p WHERE p.projectName = :projectName")
    Long countDistinctTaskTypesByProjectName(@Param("projectName") String projectName);
}
