package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectTaskType.ProjectTaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectTaskTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectTaskTypeService {

    private final ProjectTaskTypeRepository projectTaskTypeRepository;

    @Autowired
    public ProjectTaskTypeService(ProjectTaskTypeRepository projectTaskTypeRepository) {
        this.projectTaskTypeRepository = projectTaskTypeRepository;
    }

    /**
     * Creates a new project-task type association.
     *
     * @param projectTaskType The ProjectTaskType entity to create.
     * @return The saved ProjectTaskType entity.
     */
    @Transactional
    public ProjectTaskType createProjectTaskType(ProjectTaskType projectTaskType) {
        return projectTaskTypeRepository.save(projectTaskType);
    }

    /**
     * Finds a project-task type association by its composite ID.
     *
     * @param projectTaskTypeId The composite ID of the project-task type association.
     * @return An Optional containing the ProjectTaskType entity if found, otherwise empty.
     */
    public Optional<ProjectTaskType> findProjectTaskTypeById(ProjectTaskType.ProjectTaskTypeId projectTaskTypeId) {
        return projectTaskTypeRepository.findById(projectTaskTypeId);
    }

    /**
     * Finds all project-task type associations.
     *
     * @return A list of all ProjectTaskType entities.
     */
    public List<ProjectTaskType> findAllProjectTaskTypes() {
        return projectTaskTypeRepository.findAll();
    }

    /**
     * Finds project-task type associations by project name.
     *
     * @param projectName The name of the project.
     * @return A list of ProjectTaskType entities associated with the project.
     */
    public List<ProjectTaskType> findProjectTaskTypesByProjectName(String projectName) {
        return projectTaskTypeRepository.findByProjectName(projectName);
    }

    /**
     * Finds project-task type associations by task type name.
     *
     * @param taskTypeName The name of the task type.
     * @return A list of ProjectTaskType entities associated with the task type.
     */
    public List<ProjectTaskType> findProjectTaskTypesByTaskTypeName(String taskTypeName) {
        return projectTaskTypeRepository.findByTaskTypeName(taskTypeName);
    }

    /**
     * Finds project-task type associations by workflow name.
     *
     * @param workflowName The name of the workflow.
     * @return A list of ProjectTaskType entities associated with the workflow.
     */
    public List<ProjectTaskType> findProjectTaskTypesByWorkflowName(String workflowName) {
        return projectTaskTypeRepository.findByWorkflowName(workflowName);
    }

    /**
     * Finds projects using a workflow by its name.
     *
     * @param workflowName The name of the workflow.
     * @return A list of Project entities that use the specified workflow.
     */
    public List<Project> findProjectsUsingWorkflowByName(String workflowName) {
        return projectTaskTypeRepository.findProjectsUsingWorkflowByName(workflowName);
    }

    /**
     * Finds task types associated with a project by its name.
     *
     * @param projectName The name of the project.
     * @return A list of TaskType entities associated with the project.
     */
    public List<TaskType> findTaskTypesByProjectName(String projectName) {
        return projectTaskTypeRepository.findTaskTypesByProjectName(projectName);
    }

    /**
     * Counts the number of distinct task types associated with a project by its name.
     *
     * @param projectName The name of the project.
     * @return The count of distinct task types for the project.
     */
    public Long countDistinctTaskTypesByProjectName(String projectName) {
        return projectTaskTypeRepository.countDistinctTaskTypesByProjectName(projectName);
    }

    /**
     * Updates a project-task type association.
     *
     * @param updatedProjectTaskType The ProjectTaskType entity with updated information.
     * @return The updated ProjectTaskType entity.
     * @throws ProjectTaskTypeNotFoundException If the project-task type association is not found.
     */
    @Transactional
    public ProjectTaskType updateProjectTaskType(ProjectTaskType updatedProjectTaskType) {
        ProjectTaskType existingProjectTaskType = projectTaskTypeRepository.findById(updatedProjectTaskType.getId())
                .orElseThrow(() -> new ProjectTaskTypeNotFoundException("Project-TaskType association not found"));

        existingProjectTaskType.setProject(updatedProjectTaskType.getProject());
        existingProjectTaskType.setId(updatedProjectTaskType.getId());
        existingProjectTaskType.setTaskType(updatedProjectTaskType.getTaskType());
        existingProjectTaskType.setWorkflow(updatedProjectTaskType.getWorkflow());

        return projectTaskTypeRepository.save(existingProjectTaskType);
    }

    /**
     * Deletes a project-task type association by its composite ID.
     *
     * @param projectTaskTypeId The composite ID of the project-task type association to delete.
     * @throws ProjectTaskTypeNotFoundException If the project-task type association is not found.
     */
    @Transactional
    public void deleteProjectTaskType(ProjectTaskType.ProjectTaskTypeId projectTaskTypeId) {
        if (!projectTaskTypeRepository.existsById(projectTaskTypeId)) {
            throw new ProjectTaskTypeNotFoundException("Project-TaskType association not found");
        }
        projectTaskTypeRepository.deleteById(projectTaskTypeId);
    }
}
